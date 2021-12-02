package cn.itcast;

import java.nio.ByteBuffer;

/**
 * @author jlz
 * @className: TestByteBufAllocate
 * @date 2021/12/2 16:32
 * @description todo
 **/
public class TestByteBufAllocate {
    public static void main(String[] args) {
        //class java.nio.HeapByteBuffer
        //class java.nio.DirectByteBuffer
        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }
}
