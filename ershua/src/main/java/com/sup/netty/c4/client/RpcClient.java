package com.sup.netty.c4.client;

import com.sup.netty.c4.message.RpcRequestMessage;
import com.sup.netty.c4.protocol.ByteToMessageCodecSharable;
import com.sup.netty.c4.protocol.ProtocolFrameDecoder;
import com.sup.netty.c4.server.handle.RpcResponseHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月06日 22:36
 */
@Slf4j
public class RpcClient {

    public static void main(String[] args) {
        //1 服务器启动器 组装netty组件 启动服务器
        NioEventLoopGroup work = new NioEventLoopGroup();
        final ByteToMessageCodecSharable messageCodecSharable = new ByteToMessageCodecSharable();
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(work)
                    .channel(NioSocketChannel.class)
                    .handler(
                            //ChannelInitializer 初始化器 连接建立后会被调用 调用后执行initChannel
                            // 传过来NioSocketChannel
                            new ChannelInitializer<NioSocketChannel>() {
                                //连接建立后被调用
                                @Override
                                protected void initChannel(NioSocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(
                                            //log只做打印 可共享
                                            new ProtocolFrameDecoder(),
                                            new LoggingHandler(),
                                            messageCodecSharable,
                                            new RpcResponseHandle()
                                    );
                                }
                            }
                    )
                    .connect(new InetSocketAddress("127.0.0.1", 8089))
                    .sync();
            Channel channel = channelFuture.channel();
            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(1,
                    "com.sup.netty.c4.server.service.HelloService"
                    , "sayHello"
                    , String.class
                    , new Class[]{java.lang.String.class}
                    , new Object[]{"zhangsan"});
            channel.writeAndFlush(rpcRequestMessage);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
        }

    }
}
