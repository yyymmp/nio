package com.sup.c2;

import static com.sup.c1.ByteBufferUtil.debugRead;

import io.netty.buffer.ByteBufAllocator;
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
public class T_NioBlockingServer {

    //单线程模式处理阻塞模式  使用 nio 来理解阻塞模式, 单线程
    public static void main(String[] args) throws IOException {
        ServerSocketChannel open = ServerSocketChannel.open();
        open.bind(new InetSocketAddress(8089));
        List<SocketChannel> socketChannels = new ArrayList<>();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(16);
        while (true){
            //accept:有客户端建立连接 该sc是与客户端通信的channel
            log.error("建立连接前");
            SocketChannel sc = open.accept(); //阻塞方法 线程停止运行 不占用cpu 没有新的连接进来线程就会阻塞在此
            log.error("建立连接后:{}",sc);
            socketChannels.add(sc);
            //数据通信
            //接受客户端数据
            for (SocketChannel socketChannel : socketChannels) {
                log.error("数据读取前:{}",socketChannel);
                socketChannel.read(byteBuffer);  //阻塞方法 线程停止运行 不占用cpu
                //切换到读模式 打印
                byteBuffer.flip();
                debugRead(byteBuffer);
                byteBuffer.clear();
                log.error("数据读取后:{}",socketChannel);
            }

        }

    }
}
