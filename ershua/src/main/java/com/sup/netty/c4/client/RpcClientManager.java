package com.sup.netty.c4.client;

import com.sup.netty.c4.message.RpcRequestMessage;
import com.sup.netty.c4.protocol.ByteToMessageCodecSharable;
import com.sup.netty.c4.protocol.ProtocolFrameDecoder;
import com.sup.netty.c4.protocol.SequenceIdGenerator;
import com.sup.netty.c4.server.handle.RpcResponseHandle;
import com.sup.netty.c4.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月06日 22:36
 */
@Slf4j
public class RpcClientManager {

    private static Channel channel = null;
    private static final Object lock = new Object();

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (lock){
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    //代理类 将普通方法调用改为rpc调用

    /**
     *
     * @param serviceClass  传入的class
     * @param <T> 获取该类的代理类
     * @return
     */
    public static <T> T getProxyService(Class<T> serviceClass){
        Class<?>[] interfaces = new Class[]{serviceClass};
        Object proxyInstance = Proxy.newProxyInstance(serviceClass.getClassLoader(), interfaces, ((proxy, method, args) -> {
            //1 将方法调用转为消息对象
            RpcRequestMessage rpcRequessage = new RpcRequestMessage(SequenceIdGenerator.nextId(),
                    serviceClass.getName()
                    , method.getName()
                    , method.getReturnType()
                    , method.getParameterTypes()
                    , args);
            //2 将消息对象发出去
            getChannel().writeAndFlush(rpcRequessage);
            //3
            return null;
        }
        ));

        return (T) proxyInstance;
    }


    public static void main(String[] args) throws IOException {

        RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(1,
                "com.sup.netty.c4.server.service.HelloService"
                , "sayHello"
                , String.class
                , new Class[]{java.lang.String.class}
                , new Object[]{"zhangsan22"});
        //getChannel().writeAndFlush(rpcRequestMessage);
        //System.in.read();
        HelloService proxyService = getProxyService(HelloService.class);
        proxyService.sayHello("lisi");
    }

    public static void initChannel() {
        NioEventLoopGroup work = new NioEventLoopGroup();
        final ByteToMessageCodecSharable messageCodecSharable = new ByteToMessageCodecSharable();
        try {
             channel = new Bootstrap()
                    .group(work)
                    .channel(NioSocketChannel.class)
                    .handler(
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
                    .sync().channel();

            //这里需要改成异步 否则调用方调用此方法返回不了
            channel.closeFuture().addListener(future -> {
                System.out.println("监听到客户端关闭");
                work.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
