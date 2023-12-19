package com.sup.netty.c4.server;

import com.sup.netty.c4.protocol.ByteToMessageCodecSharable;
import com.sup.netty.c4.protocol.ProtocolFrameDecoder;
import com.sup.netty.c4.server.handle.ChatRequestMessageHandle;
import com.sup.netty.c4.server.handle.LoginRequestMessageHandler;
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
 * @date 2023年12月17日 13:55
 */
@Slf4j
public class ChatServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        ChatRequestMessageHandle chatRequestMessageHandle = new ChatRequestMessageHandle();
        LoginRequestMessageHandler loginRequestMessageHandler = new LoginRequestMessageHandler();
        final ByteToMessageCodecSharable messageCodecSharable = new ByteToMessageCodecSharable();
        try {

            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    //log只做打印 可共享
                                    new ProtocolFrameDecoder(),
                                    new LoggingHandler(LogLevel.DEBUG),
                                    //自定义handle是否可共享?  在这个边解码器中,没有记录数据 可被共享
                                    messageCodecSharable
                            );
                            //这里可以使用SimpleChannelInboundHandler入站处理器 因为经过上面的解码器 到这里已经知道消息的具体类型
                            //只需要关注自己的消息即可
                            ch.pipeline().addLast(loginRequestMessageHandler);
                            ch.pipeline().addLast(chatRequestMessageHandle);

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
