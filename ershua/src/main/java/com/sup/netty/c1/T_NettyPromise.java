package com.sup.netty.c1;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
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
public class T_NettyPromise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //对比jdk与netty中的future 都是需要线程提交任务才能获取
        EventLoop nioEventLoopGroup = new NioEventLoopGroup().next();
        //主动创建Promise 作为结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(nioEventLoopGroup);

        new Thread(()->{
            log.error("执行计算");
            //结果放入容器
           promise.setSuccess(50);
        }).start();

        log.error("获取结果:");
        //1 同步获取
        log.error(String.valueOf(promise.get()));
        //2 异步获取 添加监听器
        promise.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.error("获取结果:");
                //1 同步获取
                log.error(String.valueOf(promise.get()));
            }
        });


    }

}
