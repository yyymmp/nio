package cn.itcast.nio;

import static cn.itcast.utils.ByteBufferUtil.debugRead;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author clearlove
 * @ClassName NioServer.java
 * @Description
 * @createTime 2021年12月04日 11:25:00
 */
@Slf4j
public class NioServer {

    public static void main(String[] args) throws IOException {
        //使用nio单线程非阻塞方式
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //创建服务器
        ServerSocketChannel open = ServerSocketChannel.open();
        //设为非阻塞模式  它影响的是accept方法
        open.configureBlocking(false);
        //绑定端口
        open.bind(new InetSocketAddress(8080));
        //客户端连接集合
        List<SocketChannel> channels = new ArrayList<>();
        //accept 建立与客户端连接
        while (true) {
            log.info("connecting  ---------");

            //非阻塞模式下    accept方法非阻塞 如果没有连接那么返回值就是一个null值
            SocketChannel socketChannel = open.accept();

            if (socketChannel != null) {
                //socketChannel 设置为非阻塞 他影响的是read方法
                socketChannel.configureBlocking(false);
                log.info("connected  ---------" + socketChannel);
                channels.add(socketChannel);
            }
            //接受客户端数据
            for (SocketChannel channel : channels) {
                log.info("before read" + channel);
                //read 线程不会停止 只是read不到数据
                int read = channel.read(byteBuffer);
                if (read > 0) {
                    log.info("after read" + channel);
                    byteBuffer.flip();
                    debugRead(byteBuffer);
                    byteBuffer.clear();
                }
            }
        }
    }
}
