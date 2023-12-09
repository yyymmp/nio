package com.sup.netty.c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月06日 22:36
 */
@Slf4j
public class T_ChannelFuyure {

    public static void main(String[] args) throws InterruptedException {
        //1 服务器启动器 组装netty组件 启动服务器
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(
                        //ChannelInitializer 初始化器 连接建立后会被调用 调用后执行initChannel
                        // 传过来NioSocketChannel
                        new ChannelInitializer<NioSocketChannel>() {
                            //连接建立后被调用
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                                //编码器
                                ch.pipeline().addLast(new StringEncoder());
                            }
                        }
                )
                //connect 是一个异步非阻塞方法(netty中很多异步非阻塞方法)
                //1 调用者不关心结果 这叫异步
                //2 当前线程不会阻塞 这叫非阻塞
                .connect(new InetSocketAddress("127.0.0.1", 8089));

        channelFuture.sync();
        Channel channel = channelFuture.channel();

        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true){
                String s = scanner.nextLine();
                if ("q".equals(s)){
                    //close也是异步 是交给其他线程执行!!!
                    //如何在关闭后执行一些操作
                    channel.close();
                    break;
                }
                channel.writeAndFlush(s);
            }

        },"input").start();

        //对未来的关闭操作后做一些操作 必须确保该操作在关闭后进行
        //1 使用同步 确定连接关闭closeFuture.sync
        //2 使用异步
        ChannelFuture closeFuture = channel.closeFuture();
        closeFuture.addListener(new ChannelFutureListener() {
            //关闭channel的线程来执行此操作
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.error("链接关闭 进行操作");
                group.shutdownGracefully();
            }
        });
    }
}
