package cn.itcast.netty.protocol.myprotocl;

import cn.itcast.netty.protocol.myprotocl.message.LoginRequestMessage;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author jlz
 * @date 2022年02月11日 23:13
 */
public class TestSerialize {

    public static void main(String[] args) {
        //编解码
        MessageCodec messageCodec = new MessageCodec();
        LoggingHandler loggingHandler =new LoggingHandler();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(loggingHandler,messageCodec,loggingHandler);

        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhanggsan","1");

        embeddedChannel.writeOutbound(loginRequestMessage);
    }
}
