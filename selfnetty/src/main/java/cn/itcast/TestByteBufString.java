package cn.itcast;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static cn.itcast.utils.ByteBufferUtil.debugAll;

/**
 * @author jlz
 * @className: TestByteBufString
 * @date 2021/12/2 18:12
 * @description todo
 **/
public class TestByteBufString {
    public static void main(String[] args) {
        //字符串转为bytebuf  string -> byte -> butebuf
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello".getBytes());
        debugAll(buffer);

        //借助charset 已切换到读模式
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode("你好");
        debugAll(byteBuffer);

        //wrap
        ByteBuffer wrap = ByteBuffer.wrap("hello".getBytes());
        debugAll(wrap);

        //###########bytebuf -> string  已在读模式 可直接转
        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer).toString());

    }
}
