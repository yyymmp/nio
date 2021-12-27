package cn.itcast.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2021年12月26日 22:19
 */
@Slf4j
class TestEventLoopGroup {

    public static void main(String[] args) {
        //一条线是继承自 j.u.c.ScheduledExecutorService 因此包含了线程池中所有的方法
        //另一条线是继承自 netty 自己的 OrderedEventExecutor，
        //提供了 boolean inEventLoop(Thread thread) 方法判断一个线程是否属于此 EventLoop
        //提供了 parent 方法来看看自己属于哪个 EventLoopGroup

        //设置两个线程数
        EventLoopGroup group  = new NioEventLoopGroup(2);
        //从事件循环组中获取事件循环对象
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        //执行普通任务
        //group.next().submit(()->{
        //    try {
        //        TimeUnit.SECONDS.sleep(1);
        //        log.info("ok");
        //    } catch (InterruptedException e) {
        //        e.printStackTrace();
        //    }
        //});

        //执行定时任务
        group.next().scheduleAtFixedRate(()->{
         log.info("定时任务"+ UUID.randomUUID());
        },0,1,TimeUnit.SECONDS);
    }
}
