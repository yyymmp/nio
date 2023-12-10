package com.sup.netty.c1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月10日 13:08
 */
@Slf4j
public class T_JdkFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //jdk future
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        //提交任务
        Future<Integer> future = executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                log.error("异步任务");
                return 50;
            }
        });
        //主线程获取任务结果 同步等待
        log.error("获取结果:"+future.get());

        executorService.shutdown();
    }

}
