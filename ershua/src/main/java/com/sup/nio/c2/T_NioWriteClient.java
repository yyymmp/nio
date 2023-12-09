package com.sup.nio.c2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年11月27日 22:35
 */
@Slf4j
public class T_NioWriteClient {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("127.0.0.1",8089));

        int count = 0;
        while (true){
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int read = sc.read(buffer);
            count += read;
            System.out.println(count);
            buffer.clear();
        }


    }
}
