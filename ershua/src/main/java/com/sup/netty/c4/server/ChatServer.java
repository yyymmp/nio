package com.sup.netty.c4.server;

import com.sup.netty.c4.protocol.ByteToMessageCodecSharable;
import com.sup.netty.c4.protocol.ProtocolFrameDecoder;
import com.sup.netty.c4.server.handle.ChatRequestMessageHandle;
import com.sup.netty.c4.server.handle.GroupChatRequestHandle;
import com.sup.netty.c4.server.handle.GroupCreateRequestMessageHandle;
import com.sup.netty.c4.server.handle.LoginRequestMessageHandler;
import com.sup.netty.c4.server.handle.QuitHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
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
        GroupCreateRequestMessageHandle groupCreateRequestMessageHandle = new GroupCreateRequestMessageHandle();
        GroupChatRequestHandle groupChatRequestHandle = new GroupChatRequestHandle();
        QuitHandle quitHandle = new QuitHandle();
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
                                    messageCodecSharable,
                                    //连接假死检测
                                    //参数1:读空闲 写空闲,如果不关心 则设为0
                                    //参数2 写空闲 参数3 读或写
                                    //若指令时间内为读到数据 则会触发事件IdleState#readIdle 如何处理该事件? 定义双向处理器中处理
                                    new IdleStateHandler(5,0,0),
                                    //同时作为出入站处理器 但这里不关心读写事件 只关心连接假死的特殊事件
                                    new ChannelDuplexHandler(){
                                        //专门响应特殊事件如读空闲事件IdleState#readIdle
                                        @Override
                                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                            IdleStateEvent event = (IdleStateEvent) evt;
                                            //事件时读空闲时间
                                            if (event.state() == IdleState.READER_IDLE) {
                                                log.error("读空闲已经5s未读到数据");
                                            }
                                        }
                                    }
                            );
                            //这里可以使用SimpleChannelInboundHandler入站处理器 因为经过上面的解码器 到这里已经知道消息的具体类型
                            //只需要关注自己的消息即可
                            ch.pipeline().addLast(loginRequestMessageHandler);
                            ch.pipeline().addLast(chatRequestMessageHandle);
                            ch.pipeline().addLast(groupCreateRequestMessageHandle);
                            ch.pipeline().addLast(groupChatRequestHandle);
                            ch.pipeline().addLast(quitHandle);

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
