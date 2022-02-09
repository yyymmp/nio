package cn.itcast.netty.server;

import cn.itcast.netty.protocol.myprotocl.MessageCodecShare;
import cn.itcast.netty.protocol.myprotocl.ProcotolFrameDecoder;
import cn.itcast.netty.server.handler.GroupChatRequestMessageHandler;
import cn.itcast.netty.server.handler.GroupCreateRequestMessageHandler;
import cn.itcast.netty.server.handler.GroupJoinRequestMessageHandler;
import cn.itcast.netty.server.handler.GroupQuitRequestMessageHandler;
import cn.itcast.netty.server.handler.LoginRequestMessageHandler;
import cn.itcast.netty.server.handler.QuitHandler;
import cn.itcast.netty.server.handler.chatRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        //可共享的handle
        LoggingHandler loggingHandler = new LoggingHandler();
        MessageCodecShare messageCodec = new MessageCodecShare();
        LoginRequestMessageHandler loginRequestMessageHandler = new LoginRequestMessageHandler();
        chatRequestMessageHandler chatRequestMessageHandler = new chatRequestMessageHandler();
        GroupCreateRequestMessageHandler groupCreateRequestMessageHandler = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler groupChatRequestMessageHandler = new GroupChatRequestMessageHandler();
        GroupJoinRequestMessageHandler groupJoinRequestMessageHandler = new GroupJoinRequestMessageHandler();
        GroupQuitRequestMessageHandler groupQuitRequestMessageHandler = new GroupQuitRequestMessageHandler();
        QuitHandler quitHandler = new QuitHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageCodec);

                    //################### 检测心跳机制
                    //用来判断读空闲过长或者写空闲过长
                    //读空闲时间  写空闲时间(写出到客户端,但迟迟没有写出去) 读空闲+写空闲
                    //超过设定时间后触发事件IdleStateEvent
                    ch.pipeline().addLast(new IdleStateHandler(5,0,0));

                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        /**
                         * 自定义事件如何相应
                         * 专门处理特殊事件
                         * @param ctx
                         * @param evt
                         * @throws Exception
                         */
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent evt1 = (IdleStateEvent) evt;
                            if (evt1.state() == IdleState.READER_IDLE) {
                                log.info("触发 5s 未读取数据");
                                //此处需要结合客户端心跳
                                ctx.channel().close();
                            }
                        }
                    });

                    //为什么使用SimpleChannelInboundHandler 消息经过解码操作 可以只关注对应的消息类型进行操作
                    ch.pipeline().addLast(loginRequestMessageHandler);
                    ch.pipeline().addLast(chatRequestMessageHandler);
                    ch.pipeline().addLast(groupCreateRequestMessageHandler);
                    ch.pipeline().addLast(groupChatRequestMessageHandler);
                    ch.pipeline().addLast(groupJoinRequestMessageHandler);
                    ch.pipeline().addLast(groupQuitRequestMessageHandler);
                    ch.pipeline().addLast(quitHandler);

                }
            });
            Channel channel = serverBootstrap.bind(9001).sync().channel();
            log.info("服务起已启动...");
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
