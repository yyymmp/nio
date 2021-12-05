package cn.itcast.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author clearlove
 * @ClassName WriteClient.java
 * @Description
 * @createTime 2021年12月05日 22:18:00
 */
public class WriteClient {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));

        //接受数据
        int count = 0;
        while (true) {
            ByteBuffer allocate = ByteBuffer.allocate(1024 * 1024);
            int read = sc.read(allocate);
            count += read;
            System.out.println(count);
            //清空读取下一次
            allocate.clear();
        }
    }
}
