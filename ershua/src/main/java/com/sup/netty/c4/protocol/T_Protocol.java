package com.sup.netty.c4.protocol;

import com.sup.netty.c4.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author jlz
 * @date 2023年12月14日 22:47
 */
public class T_Protocol {

    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                //自定义协议配合LengthFieldBasedFrameDecoder实现粘包半包问题
                //总共16字节 最后四位是长度  长度便宜是12
                //new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new LoggingHandler(),
                new MessageCodec()
        );
        //encode
        LoginRequestMessage requestMessage = new LoginRequestMessage("zhangsan", "123");
        channel.writeOutbound(requestMessage);

        //decode
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

        new MessageCodec().encode(null, requestMessage, buffer);
        //模拟半包情况 若不加帧解码器 则会出现半包情况 导致协议解码报错
        //加了帧解码器,如果出现半包 会等待数据继续发送
        ByteBuf s1 = buffer.slice(0, 15);
        channel.writeInbound(s1);
        //channel.writeInbound(buffer);
    }
}
