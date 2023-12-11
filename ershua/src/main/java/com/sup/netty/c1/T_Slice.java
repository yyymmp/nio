package com.sup.netty.c1;

import static com.sup.netty.c1.T_Bytebuf.log;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author jlz
 * @date 2023年12月11日 22:22
 */
public class T_Slice {
    //slice是netty对零拷贝的体现之一

    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        log(byteBuf);
        //对bytebuf进行切片 没有数据复制发生
        ByteBuf f1 = byteBuf.slice(0, 5);
        ByteBuf f2 = byteBuf.slice(5, 5);
        log(f1);
        log(f2);
        //修改切片1  会同步修改原始bytebuf 因为他们共用的是一片内存
        System.out.println("================");
        f1.setByte(1, 'z');
        log(byteBuf);
        log(f1);
    }

}
