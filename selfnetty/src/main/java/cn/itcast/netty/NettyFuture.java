package cn.itcast.netty;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2022年01月03日 12:02
 */
@Slf4j
class NettyFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        EventLoop eventLoop = group.next();

        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.info("计算结果");
                TimeUnit.SECONDS.sleep(1);
                return 20;
            }
        });

        //netty 同步方式
        Integer integer = future.get();
        log.info("结果:{}",integer);

        //netty 异步结果 使用亦不会回调
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            //任务结果返回后 调用此方法
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.info("接收结果:{}",future.getNow());
            }
        });


    }
}
