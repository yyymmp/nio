package cn.itcast.nio;

import static cn.itcast.utils.ByteBufferUtil.debugAll;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * @author clearlove
 * @ClassName MutiNioServer.java
 * @Description
 * @createTime 2021年12月13日 23:01:00
 */
@Slf4j
public class MutiNioServer {

    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        Selector boss = Selector.open();
        SelectionKey bossKey = serverSocketChannel.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);

        serverSocketChannel.bind(new InetSocketAddress(8081));
        //创建固定数量的work
        //Worker worker = new Worker("work-0");
        Worker[] workers = new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("work-" + i);
        }
        AtomicInteger index = new AtomicInteger(0);
        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    socketChannel.configureBlocking(false);
                    log.debug("connected----" + socketChannel.getRemoteAddress());
                    //与work关联 关联到work的选择器 读写事件交给work
                    log.debug("before register");
                    //轮询算法
                    workers[index.getAndIncrement() % workers.length].register(socketChannel);

                    log.debug("after register");
                }


            }


        }

    }

    static class Worker implements Runnable {

        volatile boolean start = false;

        String name;

        Selector selector;

        Thread thread;

        ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<Runnable>();

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel socketChannel) throws IOException {
            //初始化线程和selector
            if (!start) {
                thread = new Thread(this, name);
                selector = Selector.open();
                start = true;
                thread.start();
            }
            //利用队列来传递线程之间的信息
            queue.add(() -> {
                try {
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            //第一次注册事件时手动放行selector.select();
            selector.wakeup();
        }

        @Override
        public void run() {
            //work监测读写事件
            while (true) {
                try {
                    selector.select();
                    Runnable task = queue.poll();
                    if (null != task) {
                        task.run();
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            log.debug("read----");
                            //可读
                            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                            //拿到客户端通道
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.read(byteBuffer);
                            byteBuffer.flip();
                            debugAll(byteBuffer);
                        } else if (key.isWritable()) {

                        }
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }


        }
    }
}
