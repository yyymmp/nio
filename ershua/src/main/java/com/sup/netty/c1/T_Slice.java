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
        //切片后 最大容量不允许再增加
        ByteBuf f1 = byteBuf.slice(0, 5);
        ByteBuf f2 = byteBuf.slice(5, 5);
        log(f1);
        log(f2);
        //如果释放原始byteBuf 则会影响切片 如何解决 -> 切片使用f1.retain() 使用引用技术+1
        byteBuf.release();
        //修改切片1  会同步修改原始bytebuf 因为他们共用的是一片内存
        System.out.println("================");
        f1.setByte(1, 'z');
        log(byteBuf);
        log(f1);

        //duplicate也是零拷贝的体现,与slice不同的是 duplicate复制的是整个原始数据
        ByteBuf d1 = byteBuf.duplicate();

        //copy开头方法 会将底层内存数据进行深拷贝，因此无论读写，都与原始 ByteBuf无关
        ByteBuf c1 = byteBuf.copy();
    }

}
