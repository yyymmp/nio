package cn.itcast;

import static cn.itcast.utils.ByteBufferUtil.debugAll;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author clearlove
 * @ClassName TestScatteringReads.java
 * @Description
 * @createTime 2021年12月02日 23:31:00
 */
public class TestScatteringReads {

    public static void main(String[] args) {
        ByteBuffer b1 = ByteBuffer.allocate(3);
        ByteBuffer b2 = ByteBuffer.allocate(3);
        ByteBuffer b3 = ByteBuffer.allocate(3);

        try (FileChannel channel = new RandomAccessFile("D:\\down_package\\maven_work\\nio\\selfnetty\\src\\a.txt", "r").getChannel()) {
            //分散读取  因为预先知道每段诗句长度
            channel.read(new ByteBuffer[]{b1, b2, b3});

            debugAll(b1);
            debugAll(b2);
            debugAll(b3);


        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }
}
