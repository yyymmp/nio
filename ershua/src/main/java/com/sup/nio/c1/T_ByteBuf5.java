package com.sup.nio.c1;

import static com.sup.nio.c1.ByteBufferUtil.debugAll;

import java.nio.ByteBuffer;

/**
 * @author jlz
 * @date 2023年11月23日 18:54
 */
public class T_ByteBuf5 {

    //解决半包 粘包问题

    public static void main(String[] args) {
        //practice
        ByteBuffer buffer = ByteBuffer.allocate(32);
        //模拟网络半包 粘包现象
        buffer.put("hello,world\nIm zhangSan\nho".getBytes());
        split(buffer);
        buffer.put("w are you?\n".getBytes());
        split(buffer);
    }

    private static void split(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        for (int i = 0; i < byteBuffer.limit(); i++) {
            if (byteBuffer.get(i) == '\n') {
                //每次消息结尾的位置
                int len = i + 1 - byteBuffer.position();
                //使用一个新的bytebuf来接受
                ByteBuffer tar = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    tar.put(byteBuffer.get());
                }
                debugAll(tar);

            }

        }
        //将已读去除
        byteBuffer.compact();


    }
}
