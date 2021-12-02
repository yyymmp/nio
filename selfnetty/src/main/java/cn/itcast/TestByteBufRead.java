package cn.itcast;

import java.nio.ByteBuffer;

import static cn.itcast.utils.ByteBufferUtil.debugAll;

/**
 * @author jlz
 * @className: TestByteBufRead
 * @date 2021/12/2 15:45
 * @description todo
 **/
public class TestByteBufRead {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put((byte) 0x61);
        debugAll(byteBuffer);
        byteBuffer.put(new byte[]{ 0x62,0x63,0x64});
        debugAll(byteBuffer);

//        System.out.println(byteBuffer.get());
//        切换读模式
        byteBuffer.flip();
        System.out.println(byteBuffer.get());
        debugAll(byteBuffer);
        //切换写模式 但保留未读位置
        byteBuffer.compact();
        debugAll(byteBuffer);
    }
}
