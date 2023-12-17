package com.sup.netty.c4.client;

import com.sup.netty.c4.message.LoginRequestMessage;
import com.sup.netty.c4.protocol.ByteToMessageCodecSharable;
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
import java.net.InetSocketAddress;

/**
 * @author jlz
 * @date 2023年12月06日 22:36
 */
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
                                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0));
                                    ch.pipeline().addLast(messageCodecSharable);
                                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                        //channel 建立成功后触发
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            LoginRequestMessage requestMessage = new LoginRequestMessage("zhangsan", "123");
                                            ctx.writeAndFlush(requestMessage);
                                            //ByteBuf buffer = ctx.alloc().buffer();
                                            //buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                            //ctx.writeAndFlush(buffer);
                                            //ctx.writeAndFlush(requestMessage);
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
