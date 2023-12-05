package com.sup.c2;

import static com.sup.c1.ByteBufferUtil.debugAll;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年11月27日 22:35
 */
@Slf4j
public class T_NioMutiThreadSelectorServer {

    //多线程版本配合selector
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        //boss selector配合boss thread 仅处理连接事件
        Selector boss = Selector.open();
        //密集型运算 设置为核心数+1  io型运算 设置为 1+ (IO计算耗时/CPU计算耗时)
        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("work-"+i);
        }
        ssc.register(boss,SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8089));
        AtomicInteger index= new AtomicInteger();
        while (true){
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.error("有连接建立...客户端:{}",sc.getRemoteAddress());
                    //将SocketChannel关联上work 如何关联上 只需要将这个SocketChannel注册到work的selector上即可绑定
                    //selector已经在run方法中运行 也可以即时关联
                    //selector在多个线程中 若在某一个线程中正在阻塞select(),那么在另一个线程中时无法注册的
                    log.error("before register work:");
                    //轮询注册
                    workers[index.getAndIncrement()%workers.length].regis(sc);
                    log.error("after register work:");
                }
            }
        }


    }
    //work 负责检测读写事件
    static class  Worker implements Runnable{
        private Selector selector;
        private Thread thread;
        private String name;
        private volatile boolean start = false;
        //两个线程传递数据 而且还不是立刻执行 可以使用队列解耦
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
        public Worker(String name) {
            this.name = name;
        }

        public Worker(Selector selector, Thread thread, String name) {
            this.selector = selector;
            this.thread = thread;
            this.name = name;
        }

        public void  regis(SocketChannel sc) throws IOException {
            if (!start){
                //初始化线程和selector
                thread = new Thread(this,name);
                selector = Selector.open();
                thread.start();
                start = true;
            }
            sc.register(selector,SelectionKey.OP_READ,null);
            //防止work线程中select阻塞导致read事件无法注册
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true){
                try {
                    selector.select();
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()){
                            SocketChannel sc = (SocketChannel)key.channel();
                            log.error("read..{}",sc.getRemoteAddress());
                            //简单处理 暂不处理之前考虑的客户端异常断开 半包粘包 写入数据过多等问题处理
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            sc.read(buffer);
                            //读模式
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        }
    }

}
