package com.sup.nio.c1;

import static com.sup.nio.c1.ByteBufferUtil.debugAll;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author jlz
 * @date 2023年11月23日 18:54
 */
public class T_ByteBuf4 {

    public static void main(String[] args) {
        //字符串与bytebuffer互转
        //1
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(16);
        byte[] bytes = "hello".getBytes();
        byteBuffer.put(bytes);
        debugAll(byteBuffer);
        //2 charset使用encode方法 写入且直接转为读模式
        System.out.println(Charset.defaultCharset());
        ByteBuffer hello = StandardCharsets.UTF_8.encode("hello");
        debugAll(hello);
        hello.compact();
        debugAll(hello);
        //3 wrap 写入且直接转为读模式
        ByteBuffer wrap = ByteBuffer.wrap("hello".getBytes());
        debugAll(wrap);

        //bytebuff -> str  需要bytebuf切换为读模式才可以decode
        CharBuffer decode = StandardCharsets.UTF_8.decode(wrap);
        System.out.println(decode.toString());
    }
}
