package cn.itcast.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author clearlove
 * @ClassName Client.java
 * @Description
 * @createTime 2021年12月04日 22:25:00
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8081));
        sc.write(Charset.defaultCharset().encode("hello"));

        System.out.println("wait");

    }
}
