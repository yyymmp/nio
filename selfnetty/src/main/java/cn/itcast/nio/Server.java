package cn.itcast.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static cn.itcast.utils.ByteBufferUtil.debugRead;

/**
 * @author jlz
 * @className: Server
 * @date 2021/12/3 15:53
 * @description todo
 **/
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        //使用单线程阻塞方式
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //创建服务器
        ServerSocketChannel open = ServerSocketChannel.open();
        //绑定端口
        open.bind(new InetSocketAddress(8080));
        //客户端连接集合
        List<SocketChannel> channels = new ArrayList<>();
        //accept 建立与客户端连接
        while (true) {
            log.info("connecting  ---------");
            //socketChannel 与客户端之间通信
            //accept 阻塞方法 阻塞:不会使用cpu 线程暂停
            SocketChannel socketChannel = open.accept();
            log.info("connected  ---------"+socketChannel);
            channels.add(socketChannel);
            //接受客户端数据
            for (SocketChannel channel : channels) {
                log.info("before read"+channel);
                //read 阻塞方法
                channel.read(byteBuffer);
                log.info("after read"+channel);
                byteBuffer.flip();
                debugRead(byteBuffer);
                byteBuffer.clear();
            }
        }
    }
}
