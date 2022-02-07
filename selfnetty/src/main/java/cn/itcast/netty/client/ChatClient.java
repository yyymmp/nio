package cn.itcast.netty.client;

import cn.itcast.netty.protocol.myprotocl.MessageCodecShare;
import cn.itcast.netty.protocol.myprotocl.ProcotolFrameDecoder;
import cn.itcast.netty.protocol.myprotocl.message.ChatRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupChatRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupCreateRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupJoinRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupMembersRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupQuitRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.LoginRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.LoginResponseMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatClient {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        CountDownLatch countDownLatch  = new CountDownLatch(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            LoggingHandler loggingHandler = new LoggingHandler();
            MessageCodecShare messageCodec = new MessageCodecShare();
            AtomicBoolean atomicBoolean = new AtomicBoolean();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    //与业务无关的处理器
                    //入站
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    //入站+出战
                    //ch.pipeline().addLast(loggingHandler);
                    //入站+出战
                    ch.pipeline().addLast(messageCodec);

                    //与业务相关的handler
                    //在连接建立后发送一个登录请求 可以添加一个handle
                    ch.pipeline().addLast("client handle",new ChannelInboundHandlerAdapter(){
                        //连接建立后触发
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //使用业务线程接受用户输入
                            new Thread(()->{
                                //负责接受用户控制台输入 想服务器输入各种消息
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码");
                                String pwd = scanner.nextLine();

                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username,pwd);
                                //观察该消息是如何传播的 ,当前是一个入站处理器 ,写入了数据,就会经过出战处理器
                                //依次经过messageCodec  loggingHandler head(内置)
                                ctx.writeAndFlush(loginRequestMessage);

                                System.out.println("等到进一步输入");

                                try {
                                    //需要等待服务器返回进行下一步动作
                                    countDownLatch.await();

                                    if (!atomicBoolean.get()){
                                        System.out.println("登录失败");
                                        //关闭通道 触发关闭时间
                                        ctx.channel().close();
                                        return;
                                    }

                                    //登录成功
                                    while (true) {
                                        System.out.println("==================================");
                                        System.out.println("send [username] [content]");
                                        System.out.println("gsend [group name] [content]");
                                        System.out.println("gcreate [group name] [m1,m2,m3...]");
                                        System.out.println("gmembers [group name]");
                                        System.out.println("gjoin [group name]");
                                        System.out.println("gquit [group name]");
                                        System.out.println("quit");
                                        System.out.println("==================================");

                                        String command = scanner.nextLine();
                                        //单聊业务
                                        String[] s = command.split(" ");
                                        switch (s[0]){
                                            //单聊业务
                                            case "send":
                                                ChatRequestMessage chatRequestMessage = new ChatRequestMessage(username,s[1],s[2]);
                                                ctx.writeAndFlush(chatRequestMessage);
                                                break;
                                            case "gsend":
                                                GroupChatRequestMessage groupChatRequestMessage = new GroupChatRequestMessage(username,s[1],s[2]);
                                                ctx.writeAndFlush(groupChatRequestMessage);
                                                break;
                                            case "gcreate":
                                                List<String> list = Arrays.asList(s[2].split(","));
                                                Set<String> set =  new HashSet<>(list);
                                                GroupCreateRequestMessage groupCreateRequestMessage = new GroupCreateRequestMessage(s[1],set);
                                                ctx.writeAndFlush(groupCreateRequestMessage);
                                                break;
                                            case "gmembers":
                                                GroupMembersRequestMessage groupMembersRequestMessage = new GroupMembersRequestMessage(s[1]);
                                                ctx.writeAndFlush(groupMembersRequestMessage);
                                                break;
                                            case "gjoin":
                                                GroupJoinRequestMessage groupJoinRequestMessage = new GroupJoinRequestMessage(username,s[1]);
                                                ctx.writeAndFlush(groupJoinRequestMessage);
                                                break;
                                            case "gquit":
                                                GroupQuitRequestMessage groupQuitRequestMessage = new GroupQuitRequestMessage(username,s[1]);
                                                ctx.writeAndFlush(groupQuitRequestMessage);
                                                break;
                                            case "quit":
                                                ctx.channel().close();
                                                return;
                                        }
                                        if (command.startsWith("send")){

                                        }
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            },"system in").start();
                        }

                        /**
                         * channelRead接受服务器返回
                         * @param ctx
                         * @param msg
                         * @throws Exception
                         */
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            //如何将服务起返回的消息传入接受输入线程 很多方法 这里可以使用count
                            if(msg instanceof LoginResponseMessage){
                                LoginResponseMessage loginResponseMessage = (LoginResponseMessage) msg;
                                if (loginResponseMessage.isSuccess()){
                                    atomicBoolean.set(true);
                                }else {
                                    atomicBoolean.set(false);
                                }
                                countDownLatch.countDown();
                            }
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 9001).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
