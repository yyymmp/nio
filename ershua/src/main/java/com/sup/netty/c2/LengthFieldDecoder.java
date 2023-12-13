package com.sup.netty.c2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author jlz
 * @date 2023年12月13日 22:05
 */
public class LengthFieldDecoder {

    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                //第一个参数整个消息最大长度  超过则异常
                new LengthFieldBasedFrameDecoder(1024,0,4,1,5),
                new LoggingHandler(LogLevel.DEBUG)
        );
        //定义协议  4字节表示长度+实际内容
        ByteBuf buffer = channel.alloc().buffer();
        send(buffer, "hello world");
        send(buffer, "jializhong");
        channel.writeInbound(buffer);


    }

    private static void send(ByteBuf buffer, String msg) {
        byte[] bytes = msg.getBytes();
        int length = bytes.length;
        //写入长度 int 正好4个字节
        buffer.writeInt(length);
        buffer.writeByte(1);
        //写入内容
        buffer.writeBytes(bytes);
    }

}
