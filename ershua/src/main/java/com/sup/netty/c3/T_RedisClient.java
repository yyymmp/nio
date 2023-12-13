package com.sup.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import java.util.Random;

/**
 * @author jlz
 * @date 2023年12月13日 22:21
 */
public class T_RedisClient {
    //实现redis协议

    public static void main(String[] args) {
        send();
    }

    public static void send() {
        //1 服务器启动器 组装netty组件 启动服务器
        NioEventLoopGroup work = new NioEventLoopGroup();
        //换行符\r\n
        byte[] line = {13, 10};
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
                                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                        //channel 建立成功后触发
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            //实现redis协议
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            buffer.writeBytes("*3".getBytes());
                                            //每个之间需要换行
                                            buffer.writeBytes(line);
                                            buffer.writeBytes("$3".getBytes());
                                            buffer.writeBytes(line);
                                            buffer.writeBytes("set".getBytes());
                                            buffer.writeBytes(line);
                                            buffer.writeBytes("$4".getBytes());
                                            buffer.writeBytes(line);
                                            buffer.writeBytes("name".getBytes());
                                            buffer.writeBytes(line);
                                            buffer.writeBytes("$3".getBytes());
                                            buffer.writeBytes(line);
                                            buffer.writeBytes("jlz".getBytes());
                                            buffer.writeBytes(line);
                                            ctx.writeAndFlush(buffer);
                                        }

                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            ByteBuf byteBuf = (ByteBuf)msg;
                                            System.out.println(byteBuf.toString());
                                        }
                                    });
                                }
                            }
                    )
                    .connect(new InetSocketAddress("127.0.0.1", 6379))
                    .sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
        }
    }
}
