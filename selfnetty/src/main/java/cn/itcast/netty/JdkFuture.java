package cn.itcast.netty;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2022年01月02日 23:13
 */
@Slf4j
class JdkFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        Future<Integer> future = threadPool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("计算结果");
                TimeUnit.SECONDS.sleep(1);
                return 20;
            }
        });
        //主线程同步阻塞湖区方法
        Integer integer = future.get();
        log.info("结果:{}",integer);
        threadPool.shutdown();
    }
}
