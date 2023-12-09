package com.sup.netty.c1;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月09日 16:21
 */
@Slf4j
public class T_EventLoop {

    public static void main(String[] args) {
        //事件循环组 有多种实现
        //NioEventLoopGroup 可以处理io(select) 也可执行普通任务,定时任务
        //DefaultEventLoopGroup 可以处理普通任务与定时任务 不能处理io事件
        //EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        //不指定参数时 线程数为核心数*2
        EventLoopGroup group = new DefaultEventLoopGroup(2);
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        //执行普通任务
        /*
        group.next().submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.error("ok");
        });
        log.error("main");
         */
        //执行定时任务(实现连接的保护)
        group.next().scheduleAtFixedRate(()->{
            log.error("ok");
        },1,1, TimeUnit.SECONDS);
    }
}
