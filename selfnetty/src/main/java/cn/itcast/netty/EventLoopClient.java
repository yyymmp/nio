package cn.itcast.netty;

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
 * @date 2021年12月26日 19:50
 */
@Slf4j
class EventLoopClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                //添加处理器
                //ChannelInitializer:初始化器会在连接建立后被调用 执行initChannel
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        //将发送的字符串编码
                        ch.pipeline().addLast(new StringEncoder());
                    }

                })
                //connect:异步非阻塞 真正连接操作的是另外一个线程去做 -> nio线程  并且这个连接操作是比较慢的
                .connect(new InetSocketAddress("127.0.0.1", 8080));
        //sync的作用: 上方连接还没有建立,这里直接拿到channel,其实是一个连接建立前的channel,此时发消息根本发布到服务起
        //所以需要在此调用sync 阻塞当前线程 直到连接建立
        channelFuture.sync();
        Channel channel = channelFuture.channel();

        //解决方案2 使用异步处理结果 传递回调对象
        //channelFuture.addListener(new ChannelFutureListener() {
        //    //在nio线程中建立连接后会调用 此时拿channel
        //    @Override
        //    public void operationComplete(ChannelFuture future) throws Exception {
        //        Channel channel = future.channel();
        //        System.out.println(channel);
        //        channel.writeAndFlush("hello");
        //    }
        //});
        //新起一个线程专门接受输入
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.next();
                if (line.equals("q")) {
                    //close 也是异步操作
                    channel.close();
                    //不可以在这里关闭
                    //log.info("处理关闭");

                    break;
                }

                channel.writeAndFlush(line);
            }

        }, "input").start();

        //通过channel获取关闭对象  1 同步关闭 2 异步关闭处理
        ChannelFuture channelFuture1 = channel.closeFuture();
        //1
        //channelFuture1.sync();
        //log.info("处理关闭");

        //2
        channelFuture1.addListener(new ChannelFutureListener() {
            //channel被关闭后回调函数
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("回调关闭");

                group.shutdownGracefully();
            }
        });
    }
}
