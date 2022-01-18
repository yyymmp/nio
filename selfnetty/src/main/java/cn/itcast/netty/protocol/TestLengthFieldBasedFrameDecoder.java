package cn.itcast.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author jlz
 * @date 2022年01月18日 22:40
 */
public class TestLengthFieldBasedFrameDecoder {

    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                //最后一个参数  解码后的数据是否有偏移 如解码后的内容不需要字节长度 剥离四个字节的长度
                //just: 从长度内容后 需要偏移几个字节 才是内容长度
                new LengthFieldBasedFrameDecoder(1024,0,4,1,4),
                new LoggingHandler(LogLevel.INFO)
        );

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        byte[] bytes = "hello, world".getBytes();
        int length = bytes.length;
        //先写入长度 再写入内容
        buffer.writeInt(length);
        buffer.writeByte(1);
        buffer.writeBytes(bytes);

        buffer.writeInt("hi".getBytes().length);
        buffer.writeByte(1);
        buffer.writeBytes("hi".getBytes());

        embeddedChannel.writeInbound(buffer);

    }
}
