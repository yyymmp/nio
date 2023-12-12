package com.sup.netty.c2;

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
 * @date 2023年12月06日 22:36
 */
public class LineBaseClient {

    public static void main(String[] args) {
            send();
    }
    public static void  send(){
        //1 服务器启动器 组装netty组件 启动服务器
        NioEventLoopGroup work = new NioEventLoopGroup();
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
                                            Random r = new Random();
                                            char c = 'a';
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            for (int i = 0; i < 10; i++) {
                                                for (int j = 1; j <= r.nextInt(16)+1; j++) {
                                                    buffer.writeByte((byte) c);
                                                }
                                                //10是换行符
                                                buffer.writeByte('\r');
                                                buffer.writeByte('\n');
                                                c++;
                                            }
                                            ctx.writeAndFlush(buffer);
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
