package com.sup.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月13日 22:21
 */
@Slf4j
public class T_HttpServerServer {
    //实现http服务端

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    //2 半包现象 设置接受缓冲区 设的比较小
                    .option(ChannelOption.SO_RCVBUF, 10)
                    .childHandler(
                            new ChannelInitializer<NioSocketChannel>() {

                                @Override
                                protected void initChannel(NioSocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                                    ch.pipeline().addLast(new HttpServerCodec());
                                    ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                        //只接受自己感兴趣的的类型
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                            //获取请求
                                            log.error(msg.uri());

                                            //返回响应
                                            //1 http协议版本
                                            DefaultFullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                            byte[] bytes = "<h1>hello</h1>".getBytes();
                                            response.content().writeBytes(bytes);
                                            //不加内容长度 浏览器会认为服务端数据还没发完 会一直转圈等待
                                            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
                                            ctx.writeAndFlush(response);
                                        }
                                    });
                                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            //msg 经过http解码器解码后的结果是什么
                                            //会被打印两部分 请求头与请求体 //DefaultHttpRequest LastHttpContent$1
                                            log.error("{}", msg.getClass());
                                            //若要对两部分分别进行处理 可以使用if else判断
                                            //更优雅的方法是使用一个simple入站处理器 只关心自己的指定类型才会接受
                                            /*
                                            if (msg instanceof HttpRequest){

                                            }else if (msg instanceof HttpResponse){

                                            }
                                             */

                                        }
                                    });
                                }

                            })
                    .bind(8089).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }


}
