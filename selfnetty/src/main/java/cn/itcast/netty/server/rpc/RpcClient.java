package cn.itcast.netty.server.rpc;

import cn.itcast.netty.protocol.myprotocl.MessageCodecShare;
import cn.itcast.netty.protocol.myprotocl.ProcotolFrameDecoder;
import cn.itcast.netty.protocol.myprotocl.message.RpcRequestMessage;
import cn.itcast.netty.server.handler.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2022年02月16日 21:23
 */
@Slf4j
public class RpcClient {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {

            LoggingHandler loggingHandler = new LoggingHandler();
            MessageCodecShare messageCodec = new MessageCodecShare();
            RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();
            bootstrap.channel(NioSocketChannel.class)
                    .group(group)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProcotolFrameDecoder());

                            ch.pipeline().addLast(loggingHandler);
                            ch.pipeline().addLast(messageCodec);

                            ch.pipeline().addLast(rpcResponseMessageHandler);
                        }
                    });

            Channel channel = bootstrap.connect("localhost", 9001).sync().channel();
            log.info("客户端已连接...");
            //调用一次远程接口
            RpcRequestMessage message = new RpcRequestMessage(
                    1,
                    "cn.itcast.netty.server.service.HelloRpcService",
                    "sayHello",
                    String.class,
                    //参数类型
                    new Class[]{String.class},
                    //参数值
                    new Object[]{"李四"}
            );
            channel.writeAndFlush(message);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

}
