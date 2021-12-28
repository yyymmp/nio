package cn.itcast.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2021年12月27日 23:08
 */
@Slf4j
class EventLoopServer {

    public static void main(String[] args) {
        //创建独立的EventLoopGroup 处理耗时较长的操作 而不是使用NioEventLoopGroup
        EventLoopGroup group = new DefaultEventLoopGroup();
        new ServerBootstrap()
                //细分职责
                //一个boss线程专门负责连接事件 两个work处理读写事件
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    //initChannel连接建立后被调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //nioEventLoopGroup处理该handle
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            //关注读事件 重写读事件
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.info(byteBuf.toString(Charset.defaultCharset()));
                                //两个handle产生联系 将消息传递给下一个handle
                                ctx.fireChannelRead(msg);
                            }
                        });

                        //h1使用defaultEventLoopGroup线程处理
                        ch.pipeline().addLast(group, "h2", new ChannelInboundHandlerAdapter() {
                            //关注读事件 重写读事件
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.info(byteBuf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .bind(8082);

    }
}
