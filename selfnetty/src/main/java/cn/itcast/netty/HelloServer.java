package cn.itcast.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2021年12月26日 19:12
 */
@Slf4j
class HelloServer {

    public static void main(String[] args) {
        //服务端启动器
        new ServerBootstrap()
                //线程组BossEventLoop  workerEventLoop(selector thread)   group:组 多个时间循环
                .group(new NioEventLoopGroup())
                //选择服务器ServerSocketChannel实现 这里选择Nio实现
                .channel(NioServerSocketChannel.class)
                //boss负责连接  work(child)负责读写 决定了work(child)能执行哪些操作(Handler)
                .childHandler(
                    //Channel:代表和客户端进行数据读写的通道  Initializer:负责添加别的Handler
                    new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //添加Handlerzz
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){  //自定义Handler
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                              log.info(""+msg);
                            }
                        });
                    }
                })
                .bind(8080);


    }
}
