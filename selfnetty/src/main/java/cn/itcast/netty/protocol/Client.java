package cn.itcast.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2022年01月12日 22:50
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            send();
        }
        log.info("finish");
    }

    private static void send() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = bootstrap.group(work)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    //粘包现象 客户端本意是十次消息十次发送 但在客户端是一次性接受
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                    ctx.writeAndFlush(buffer);
                                    ctx.close();
                                }

                            });

                        }
                    })
                    .connect("127.0.0.1", 8081)
                    .sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error", e);
        } finally {
            work.shutdownGracefully();
        }
    }
}
