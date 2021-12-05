package cn.itcast.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author clearlove
 * @ClassName Client.java
 * @Description
 * @createTime 2021年12月04日 22:25:00
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));

        System.out.println("wait");

    }
}
