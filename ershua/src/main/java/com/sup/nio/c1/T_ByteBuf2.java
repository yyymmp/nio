package com.sup.nio.c1;

import java.nio.ByteBuffer;

/**
 * @author jlz
 * @date 2023年11月23日 18:54
 */
public class T_ByteBuf2 {

    public static void main(String[] args) {
        ByteBuffer allocate = ByteBuffer.allocate(10);
        allocate.put((byte) 0x61); //'a'
        ByteBufferUtil.debugAll(allocate);
        allocate.put(new byte[]{0x62,0x63,0x64});
        ByteBufferUtil.debugAll(allocate);
        //读模式切换
        allocate.flip();
        ByteBufferUtil.debugAll(allocate);
        System.out.println(allocate.get());
        ByteBufferUtil.debugAll(allocate);
        //转到写模式
        allocate.compact();
        ByteBufferUtil.debugAll(allocate);
    }
}
