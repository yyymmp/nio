package cn.itcast.netty.server.rpc;

import cn.itcast.netty.protocol.SequenceIdGenerator;
import cn.itcast.netty.protocol.myprotocl.MessageCodecShare;
import cn.itcast.netty.protocol.myprotocl.ProcotolFrameDecoder;
import cn.itcast.netty.protocol.myprotocl.message.RpcRequestMessage;
import cn.itcast.netty.server.handler.RpcResponseMessageHandler;
import cn.itcast.netty.server.service.HelloRpcService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import java.lang.reflect.Proxy;
import lombok.extern.slf4j.Slf4j;

/**
 * 对于rpc来说,需要一个地方获取channel 并且保持长连接 每一个客户端仅仅需要一个即可
 *
 * @author jlz
 * @date 2022年02月16日 21:23
 */
@Slf4j
public class RpcClientManager {

    private static Channel channel = null;

    private static final Object LOCK = new Object();

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            init();
            return channel;
        }
    }

    public static void init() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecShare MESSAGE_CODEC = new MessageCodecShare();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 9001).sync().channel();
            log.info("获取channel");
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }

    public static void main(String[] args) {
        Channel channel = getChannel();
        HelloRpcService proxyService = getProxyService(HelloRpcService.class);
        System.out.println(proxyService.sayHello("weide"));
        //log.info("调用返回结果:{}",weide);
        //System.out.println(proxyService.sayHello("alun"));
        //log.info("调用返回结果:{}",alun);
        //channel.writeAndFlush(new RpcRequestMessage(
        //        1,
        //        "cn.itcast.netty.server.service.HelloRpcService",
        //        "sayHello",
        //        String.class,
        //        //参数类型
        //        new Class[]{String.class},
        //        //参数值
        //        new Object[]{"李四"}
        //));
    }

    public static <T> T getProxyService(Class<T> serviceType) {
        //创建代理 来发送消息
        ClassLoader loader = serviceType.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceType};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage message = new RpcRequestMessage(
                    sequenceId,
                    serviceType.getName(),
                    method.getName(),
                    method.getReturnType(),
                    //参数类型
                    method.getParameterTypes(),
                    //参数值
                    args
            );
            getChannel().writeAndFlush(message);
            //准备一个Promise接受结果                                            接受结果的线程
            DefaultPromise<Object> objectDefaultPromise = new DefaultPromise<>(getChannel().eventLoop());
            //结果放入
            RpcResponseMessageHandler.promiseMap.put(sequenceId,objectDefaultPromise);

            //同步等待Promise结果 不能立即返回
            objectDefaultPromise.await();

            if (objectDefaultPromise.isSuccess()){
                return objectDefaultPromise.getNow();
            }
            //失败了
            throw new RuntimeException(objectDefaultPromise.cause());
        });
        return (T) o;
    }
}
