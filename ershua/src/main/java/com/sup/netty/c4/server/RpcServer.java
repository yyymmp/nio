package com.sup.netty.c4.server;

import com.sup.netty.c4.protocol.ByteToMessageCodecSharable;
import com.sup.netty.c4.protocol.ProtocolFrameDecoder;
import com.sup.netty.c4.server.handle.RpcRequestHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月25日 20:45
 */
@Slf4j
public class RpcServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        RpcRequestHandle rpcRequestHandle = new RpcRequestHandle();
        final ByteToMessageCodecSharable messageCodecSharable = new ByteToMessageCodecSharable();
        try {

            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    loggingHandler,
                                    new ProtocolFrameDecoder(),
                                    messageCodecSharable,
                                    rpcRequestHandle
                            );

                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(8089).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }


    }
}
