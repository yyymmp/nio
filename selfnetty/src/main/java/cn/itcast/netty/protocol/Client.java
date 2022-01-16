package cn.itcast.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2022年01月12日 22:50
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws InterruptedException {
        //for (int i = 0; i < 10; i++) {
        send();
        //}
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
                                    char c = '0';
                                    Random r = new Random();
                                    //十次全部放在一个bytebuf发送 因为有定长解码器 服务端也不会发生粘包问题
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    for (int i = 0; i < 10; i++) {
                                        byte[] contents = getContents(r.nextInt(8), (byte) c);
                                        c++;
                                        buffer.writeBytes(contents);
                                    }
                                    ctx.writeAndFlush(buffer);
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

    public static byte[] getContents(int length, byte b) {
        if (length > 10) {
            length = 10;
        }
        byte[] ret = new byte[10];
        for (int i = 0; i < length; i++) {
            ret[i] = b;
        }

        for (int i = length; i < 10; i++) {
            ret[i] = '_';
        }
        return ret;
    }
}
