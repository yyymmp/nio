package com.sup.netty.c1;

import static com.sup.netty.c1.T_Bytebuf.log;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

/**
 * @author jlz
 * @date 2023年12月11日 22:41
 */
public class T_CompositeByteBuf {
    //将小的buf合到一个大的buf

    public static void main(String[] args) {
        ByteBuf b1 = ByteBufAllocator.DEFAULT.buffer();
        b1.writeBytes(new byte[]{1,2,3,4});

        ByteBuf b2 = ByteBufAllocator.DEFAULT.buffer();
        b2.writeBytes(new byte[]{5,6,7,8});

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        //write过程会由数据复制过程
        //buffer.writeBytes(b1).writeBytes(b2);
        log(buffer);

        //零拷贝复制
        CompositeByteBuf bufs = ByteBufAllocator.DEFAULT.compositeBuffer();
        bufs.addComponents(true,b1,b2);
        log(bufs);
    }

}
