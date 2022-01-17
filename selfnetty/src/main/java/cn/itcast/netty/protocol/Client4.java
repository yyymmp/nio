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
 * 预设长度
 * @author jlz
 * @date 2022年01月12日 22:50
 */
@Slf4j
public class Client4 {

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
                                        String string = makeString(c, r.nextInt(16)+1);
                                        int length = string.length();

                                        c++;
                                        //先写入长度 再写入内容
                                        buffer.writeInt(length);
                                        buffer.writeBytes(string.getBytes());
                                    }
                                    ctx.writeAndFlush(buffer);
                                }

                            });

                        }
                    })
                    .connect("127.0.0.1", 9091)
                    .sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error", e);
        } finally {
            work.shutdownGracefully();
        }
    }

    public static String makeString(char c,int length){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
