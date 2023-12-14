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
public class T_protocol {

    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(),
              new MessageCodec()
        );
        //encode
        LoginRequestMessage requestMessage = new LoginRequestMessage("zhangsan","123");
        channel.writeOutbound(requestMessage);

        //decode
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

        new MessageCodec().encode(null,requestMessage,buffer);

        channel.writeInbound(buffer);
    }
}
