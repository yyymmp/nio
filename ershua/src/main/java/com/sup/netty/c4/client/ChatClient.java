package com.sup.netty.c4.client;

import com.sup.netty.c4.message.ChatRequestMessage;
import com.sup.netty.c4.message.GroupChatRequestMessage;
import com.sup.netty.c4.message.GroupCreateRequestMessage;
import com.sup.netty.c4.message.GroupJoinRequestMessage;
import com.sup.netty.c4.message.GroupMembersRequestMessage;
import com.sup.netty.c4.message.GroupQuitRequestMessage;
import com.sup.netty.c4.message.LoginRequestMessage;
import com.sup.netty.c4.message.LoginResponseMessage;
import com.sup.netty.c4.protocol.ByteToMessageCodecSharable;
import com.sup.netty.c4.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月06日 22:36
 */
@Slf4j
public class ChatClient {

    public static void main(String[] args) {
        //1 服务器启动器 组装netty组件 启动服务器
        NioEventLoopGroup work = new NioEventLoopGroup();
        final ByteToMessageCodecSharable messageCodecSharable = new ByteToMessageCodecSharable();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(work)
                    .channel(NioSocketChannel.class)
                    .handler(
                            //ChannelInitializer 初始化器 连接建立后会被调用 调用后执行initChannel
                            // 传过来NioSocketChannel
                            new ChannelInitializer<NioSocketChannel>() {
                                //连接建立后被调用
                                @Override
                                protected void initChannel(NioSocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(
                                            //log只做打印 可共享
                                            new ProtocolFrameDecoder(),

                                            new LoggingHandler(LogLevel.DEBUG),

                                            messageCodecSharable

                                    );
                                    ch.pipeline().addLast("client handle", new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            log.error("{}", msg);
                                            if (msg instanceof LoginResponseMessage) {
                                                LoginResponseMessage message = (LoginResponseMessage) msg;
                                                if (message.isSuccess()) {
                                                    //登录成功
                                                    LOGIN.set(true);
                                                }
                                                //线程通信 唤醒
                                                latch.countDown();
                                            }
                                        }

                                        //连接建立时触发 进行登录
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            //负责接受控制台的输出 向服务器发送消息
                                            new Thread(() -> {
                                                Scanner scanner = new Scanner(System.in);
                                                System.out.println("请输入用户名");
                                                String name = scanner.nextLine();
                                                System.out.println("请输入密码");
                                                String pwd = scanner.nextLine();
                                                LoginRequestMessage requestMessage = new LoginRequestMessage(name, pwd);
                                                //这在一个入站处理器 如果写入内容  就会触发出战操作 从当前handle向上找
                                                ctx.writeAndFlush(requestMessage);
                                                System.out.println("......");
                                                //线程等待 等到read事件中读到服务器返回
                                                try {
                                                    latch.await();
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                //登录失败
                                                if (!LOGIN.get()) {
                                                    ctx.channel().close();
                                                    return;
                                                }
                                                //登录成功 进入菜单
                                                while (true) {
                                                    System.out.println("===============");
                                                    System.out.println("send [username] [content]");
                                                    System.out.println("gsend [group name] [content]");
                                                    System.out.println("gcreate [group name] [m1,m2,m3]");
                                                    System.out.println("gmembers [group name]");
                                                    System.out.println("gjoin [group name]");
                                                    System.out.println("gquit [group name]");
                                                    System.out.println("quit");

                                                    System.out.println("===============");
                                                    String command = scanner.nextLine();
                                                    String[] s = command.split(" ");
                                                    switch (s[0]) {
                                                        case "send":
                                                            ChatRequestMessage chatRequestMessage = new ChatRequestMessage(name, s[1], s[2]);
                                                            ctx.writeAndFlush(chatRequestMessage);
                                                            break;
                                                        case "gsend":
                                                            GroupChatRequestMessage groupChatRequestMessage = new GroupChatRequestMessage(name, s[1], s[2]);
                                                            ctx.writeAndFlush(groupChatRequestMessage);
                                                            break;
                                                        case "gcreate":
                                                            String[] split = s[2].split(",");
                                                            List<String> list = Arrays.asList(split);
                                                            Set<String> set = new HashSet<>(list);
                                                            set.add(name);
                                                            log.error("建群人数:{}",set);
                                                            GroupCreateRequestMessage groupCreateRequestMessage = new GroupCreateRequestMessage(s[1], set);
                                                            ctx.writeAndFlush(groupCreateRequestMessage);
                                                            break;
                                                        case "gmembers":
                                                            GroupMembersRequestMessage groupMembersRequestMessage = new GroupMembersRequestMessage(s[1]);
                                                            ctx.writeAndFlush(groupMembersRequestMessage);
                                                            break;
                                                        case "gjoin":
                                                            GroupJoinRequestMessage groupJoinRequestMessage  = new GroupJoinRequestMessage(name, s[1]);
                                                            ctx.writeAndFlush(groupJoinRequestMessage);
                                                            break;
                                                        case "gquit":
                                                            GroupQuitRequestMessage groupQuitRequestMessage  = new GroupQuitRequestMessage(name, s[1]);
                                                            ctx.writeAndFlush(groupQuitRequestMessage);
                                                            break;
                                                        case "quit":
                                                            ctx.channel().close();
                                                            break;
                                                    }
                                                }

                                            }).start();

                                            //
                                        }

                                    });
                                }
                            }
                    )
                    .connect(new InetSocketAddress("127.0.0.1", 8089))
                    .sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
        }

    }
}
