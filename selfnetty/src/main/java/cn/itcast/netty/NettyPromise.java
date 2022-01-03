package cn.itcast.netty;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.DefaultPromise;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2022年01月03日 12:38
 */
@Slf4j
class NettyPromise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        //手动创建接受异步结果的容器
        DefaultPromise<Integer> promise = new DefaultPromise(eventLoop);
        
        new Thread(()->{
            try {
                log.info("阻塞等待");
                TimeUnit.SECONDS.sleep(1);
                
                promise.setSuccess(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

        log.info("获取结果");
        //阻塞等到
        Integer integer = promise.get();
        log.info("结果:{}",integer);
    }
}
