package com.sup.netty.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月09日 16:35
 */
@Slf4j
public class T_EventLoopServer {

    public static void main(String[] args) {
        //2 再次细分 不让NioEventLoopGroup在handel中执行耗时比较长的操作,这样会影响其他连接的读写
        //创建一个独立的eventLoopGroup去执行耗时操作
        //tip:如果使用额外的group代替nioGroup去执行handle里面的代码 那么nioGroup有什么用呢?
        //在nio中,我们知道,nio线程是需要源源不断处理多个channel的读写事件,如果在某一个handle耗时过长,势必会影响其他channel的读写
        //在这里加上了group处理 可以解放nio线程 让nio线程继续关注select上的读写事件
        EventLoopGroup group = new DefaultEventLoopGroup();
        new ServerBootstrap()
                //1 划分职责
                //将accept事件和read事件区分 boss:处理accept  work:负责socketChannel读写
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    //连接建立后被调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //添加一个处理器 并关注读事件
                        ch.pipeline().addLast("handle1",new ChannelInboundHandlerAdapter() {

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                //bytebuf转字符串
                                log.error(byteBuf.toString(Charset.defaultCharset()));

                                //将消息传给下一个handle
                                ctx.fireChannelRead(msg);

                            }
                        }).addLast(group,"handle2",new ChannelInboundHandlerAdapter() {
                            //这里传入group 那么在handle执行时,就不是NioEventLoopGroup的线程来执行 而是交给这里的group
                            //把handle的执行权交给一个group
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                //bytebuf转字符串
                                log.error(byteBuf.toString(Charset.defaultCharset()));

                            }
                        });


                    }
                }).bind(8089);


    }

}
