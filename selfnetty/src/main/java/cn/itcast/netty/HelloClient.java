package cn.itcast.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2021年12月26日 19:50
 */
@Slf4j
class HelloClient {

    public static void main(String[] args) throws InterruptedException {
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                //添加处理器
                //ChannelInitializer:初始化器会在连接建立后被调用 执行initChannel
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //将发送的字符串编码
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("127.0.0.1",8080))
                .sync()
                .channel()
                .writeAndFlush("hello");


    }
}
