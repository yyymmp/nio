package com.sup.netty.c1;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月10日 21:25
 */
@Slf4j
public class T_Bytebuf {

    public static void main(String[] args) {
        //ByteBuf 自动扩容
        //buffer()方法获取的是直接内存
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        log(buffer);

        StringBuilder stringBuilder =new StringBuilder();
        for (int i = 0; i < 300; i++) {
            stringBuilder.append("a");
        }
        buffer.writeBytes(stringBuilder.toString().getBytes());
        log(buffer);

        //使用堆内存
        ByteBufAllocator.DEFAULT.heapBuffer();

        //netty byteBuf的池化功能 默认会开启池化功能
        System.out.println(buffer.getClass()); //PooledUnsafeDirectByteBuf
    }

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }

}
