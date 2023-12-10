package com.sup.netty.c1;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月10日 13:08
 */
@Slf4j
public class T_NettyFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //netty future 任何一个NioEventLoop只有一个线程
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        EventLoop eventLoop = nioEventLoopGroup.next();

        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                log.error("执行计算");
                return 70;
            };
        });

        //同步方式:主线程同步阻塞等待,异步任务执行完成后 唤醒主线程
        //log.error("获取结果:"+future.get());

        //异步方式
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            //由nio线程处理任务结果
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.error("接受结果:");
                log.error(future.getNow().toString());
            }
        });
    }

}
