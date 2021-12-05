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
        sc.connect(new InetSocketAddress("localhost", 8080));
        sc.write(Charset.defaultCharset().encode("1234567890qwert789456\n"));

        System.out.println("wait");

    }
}
