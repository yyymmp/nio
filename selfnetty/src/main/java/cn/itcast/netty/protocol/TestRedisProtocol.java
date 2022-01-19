package cn.itcast.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import java.nio.charset.Charset;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2022年01月18日 23:57
 */
@Slf4j
public class TestRedisProtocol {


    public static void main(String[] args) {
        final byte[] LINE = {13,10}; //回车 换行
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = bootstrap.group(work)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler());

                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                //接受redis的返回 redis返回会先经过入站处理器
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf msg1 = (ByteBuf) msg;
                                    System.out.println(msg1.toString(Charset.defaultCharset()));
                                }


                                //连接建立是就发送
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    //get(ctx);
                                    set(ctx);
                                }


                                private void get(ChannelHandlerContext ctx) {
                                    ByteBuf buf = ctx.alloc().buffer();
                                    //*2 参数数量:两个 get name
                                    buf.writeBytes("*2".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$3".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("get".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$4".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("name".getBytes());
                                    buf.writeBytes(LINE);
                                    ctx.writeAndFlush(buf);
                                }

                                private void set(ChannelHandlerContext ctx) {
                                    ByteBuf buf = ctx.alloc().buffer();
                                    //参数数量 set name value
                                    buf.writeBytes("*3".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$3".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("set".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$4".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("name".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$8".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("zhangsan".getBytes());
                                    buf.writeBytes(LINE);
                                    ctx.writeAndFlush(buf);
                                }
                            });

                        }
                    })
                    .connect("127.0.0.1", 6379)
                    .sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error", e);
        } finally {
            work.shutdownGracefully();
        }
    }
}
