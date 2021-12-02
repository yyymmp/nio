package cn.itcast;

import static cn.itcast.utils.ByteBufferUtil.debugAll;

import java.nio.ByteBuffer;

/**
 * @author clearlove
 * @ClassName TestByteBufExam.java
 * @Description 处理半包和粘包
 * @createTime 2021年12月02日 23:53:00
 */
public class TestByteBufExam {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        //截取出消息
        split(buffer);
        buffer.put("w are you?\nhaha!\n".getBytes());
        split(buffer);

    }

    /**
     * 该半包粘包比较底层
     *
     * @param buffer
     */
    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            //找到\n
            if (buffer.get(i) == '\n') {
                //读出该条完整消息
                int length = i + 1 - buffer.position();
                ByteBuffer newBuf = ByteBuffer.allocate(length);
                //从buffer读出 写入  newBuf
                for (int j = 0; j < length; j++) {
                    newBuf.put(buffer.get());
                }
                debugAll(newBuf);
            }
        }

        buffer.compact();
    }
}
