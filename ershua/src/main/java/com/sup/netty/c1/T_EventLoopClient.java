package com.sup.netty.c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月06日 22:36
 */
@Slf4j
public class T_EventLoopClient {

    public static void main(String[] args) throws InterruptedException {
        //1 服务器启动器 组装netty组件 启动服务器
        ChannelFuture channelFuture = new Bootstrap()
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
                //connect 是一个异步非阻塞方法(netty中很多异步非阻塞方法)
                //1 调用者不关心结果 这叫异步
                //2 当前线程不会阻塞 这叫非阻塞
                .connect(new InetSocketAddress("127.0.0.1", 8089));

        //channelFuture.sync();
        //如果没有sync 此时连接还没建立
        //1 使用sync方法同步处理结果 sync会阻塞当前线程直到连接建立成功
        //2 使用addListen(回调对象) 异步处理结果 最后调用线程是nio线程
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            //在nio线程中连接建立成功后, 会调用operationComplete
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                log.error("channel:{}", channel);
                channel.writeAndFlush("999");
            }
        });

    }
}
