package com.sup.netty.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author jlz
 * @date 2023年12月06日 22:36
 */
public class FixlengthServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    //调整系统的窗口滑动区
                    //.option(ChannelOption.SO_RCVBUF,10)
                    //调整netty自己的接受缓冲区
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16))
                    .childHandler(
                            new ChannelInitializer<NioSocketChannel>() {

                                @Override
                                protected void initChannel(NioSocketChannel ch) throws Exception {
                                    //约定定长长度为10
                                    ch.pipeline().addLast(new FixedLengthFrameDecoder(8));
                                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                                }

                            })
                    .bind(8089).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
