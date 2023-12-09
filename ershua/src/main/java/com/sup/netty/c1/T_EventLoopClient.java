package com.sup.netty.c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import java.net.InetSocketAddress;

/**
 * @author jlz
 * @date 2023年12月06日 22:36
 */
public class T_EventLoopClient {

    public static void main(String[] args) throws InterruptedException {
        //1 服务器启动器 组装netty组件 启动服务器
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(
                        //ChannelInitializer 初始化器 连接建立后会被调用 调用后执行initChannel
                        // 传过来NioSocketChannel
                        new ChannelInitializer<NioSocketChannel>() {
                            //连接建立后被调用
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                //编码器
                                ch.pipeline().addLast(new StringEncoder());
                            }
                        }
                )
                .connect(new InetSocketAddress("127.0.0.1", 8089))
                .sync()
                .channel();

        System.out.println(channel);
        channel.writeAndFlush("111");

    }
}
