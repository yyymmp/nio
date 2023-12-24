package com.sup.netty.c5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author jlz
 * @date 2023年12月24日 20:54
 */
public class T02SoBacklog {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    //SO_BACKLOG参数:当三次握手后,服务器accept处理不过来时,会先将该请求放在全连接队列中
                    //该参数设置队列长度 在生产环境应尽量设大一点
                    .option(ChannelOption.SO_BACKLOG,2)
                    .childHandler(
                            new ChannelInitializer<NioSocketChannel>() {
                                @Override
                                protected void initChannel(NioSocketChannel ch) throws Exception {
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
