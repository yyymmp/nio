package cn.itcast;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author clearlove
 * @ClassName GatheringWrites.java
 * @Description
 * @createTime 2021年12月02日 23:41:00
 */
public class GatheringWrites {

    public static void main(String[] args) {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("123");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");
        //集中读写
        try (FileChannel channel = new RandomAccessFile("world.txt", "rw").getChannel()) {
            //全部写入
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }
}
