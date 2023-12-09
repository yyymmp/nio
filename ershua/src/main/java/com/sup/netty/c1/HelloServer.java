package com.sup.netty.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author jlz
 * @date 2023年12月06日 22:36
 */
public class HelloServer {

    public static void main(String[] args) {
        //1 服务器启动器 组装netty组件 启动服务器
        new ServerBootstrap()
                //添加组件NioEventLoopGroup  workerEvent(selector+thread)
                .group(new NioEventLoopGroup())
                //选择服务器的ServerSocketChannel的实现 netty具有不少该实现类
                .channel(NioServerSocketChannel.class)
                //作为worker(child),需要做什么事情
                .childHandler(
                        //连接建立后被调用
                        //NioSocketChannel代表与客户端读写的通道
                        //Initializer初始化器 负责添加别的handle 在init方法中添加
                        new ChannelInitializer<NioSocketChannel>() {
                            //连接建立后被调用
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                //字符串解码器 将byteBuf转成字符串
                                ch.pipeline().addLast(new StringDecoder());
                                //自定义的handle
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    //channel触发读事件发生后
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }

                        })
                .bind(8089);
    }
}
