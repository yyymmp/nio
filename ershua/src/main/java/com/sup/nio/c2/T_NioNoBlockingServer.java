package com.sup.nio.c2;

import static com.sup.nio.c1.ByteBufferUtil.debugRead;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年11月27日 22:35
 */
@Slf4j
public class T_NioNoBlockingServer {

    //单线程模式处理非阻塞模式  使用 nio 来理解阻塞模式, 单线程
    public static void main(String[] args) throws IOException {
        ServerSocketChannel open = ServerSocketChannel.open();
        //设置为非阻塞模式 他影响的accept方法
        open.configureBlocking(false);
        open.bind(new InetSocketAddress(8089));
        List<SocketChannel> socketChannels = new ArrayList<>();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(16);
        while (true){
            //accept:有客户端建立连接 该sc是与客户端通信的channel
            //log.error("建立连接前");
            SocketChannel sc = open.accept(); //非阻塞 线程还会继续运行 如果没有连接返回的就是一个null
            if (sc != null) {
                log.error("建立连接后:{}", sc);
                //将socker channel设为非阻塞模式 影响的是read方法 read方式非阻塞
                sc.configureBlocking(false);
                socketChannels.add(sc);
            }
            //数据通信
            //接受客户端数据
            for (SocketChannel socketChannel : socketChannels) {
                //log.error("数据读取前:{}",socketChannel);
                int read = socketChannel.read(byteBuffer);//非阻塞方法 线程继续执行 如果没有读到数据 返回0
                if (read > 0) {
                    //切换到读模式 打印
                    byteBuffer.flip();
                    debugRead(byteBuffer);
                    byteBuffer.clear();
                    log.error("数据读取后:{}", socketChannel);
                }
            }


        }

    }
}
