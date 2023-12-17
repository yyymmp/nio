package com.sup.netty.c4.client;

import com.sup.netty.c4.message.LoginRequestMessage;
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
import java.util.Scanner;
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
                                    ch.pipeline().addLast("chient handle",new ChannelInboundHandlerAdapter(){
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            log.error("{}",msg);
                                        }

                                        //连接建立时触发 进行登录
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            //负责接受控制台的输出 向服务器发送消息
                                            new Thread(()->{
                                                Scanner scanner = new Scanner(System.in);
                                                System.out.println("请输入用户名");
                                                String name = scanner.nextLine();
                                                System.out.println("请输入密码");
                                                String pwd = scanner.nextLine();
                                                LoginRequestMessage requestMessage = new LoginRequestMessage(name, pwd);
                                                //这在一个入站处理器 如果写入内容  就会触发出战操作 从当前handle向上找
                                                ctx.writeAndFlush(requestMessage);

                                                //保持控制台
                                                try {
                                                    System.in.read();
                                                } catch (IOException ioException) {
                                                    ioException.printStackTrace();
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
