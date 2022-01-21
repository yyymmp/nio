package cn.itcast.netty.protocol.myprotocl;

import cn.itcast.netty.protocol.myprotocl.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author jlz
 * @date 2022年01月20日 23:02
 */
public class TestMessageCodec {

    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel  = new EmbeddedChannel(
                //配上帧解码器解决半包问题 确保解码时不会半包而转化错误   
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new LoggingHandler(),
                new MessageCodec()
        );

        //测试编码器 所以出站   出战编码
        LoginRequestMessage message = new LoginRequestMessage("zhang", "123");
        channel.writeOutbound(message);

        //测试解码器 所以入站   入战编码
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        MessageCodec messageCodec = new MessageCodec();
        //将消息写入buffer
        messageCodec.encode(null,message,buffer);
        //将encode后的消息传入 观察是否能正确解码
        channel.writeInbound(buffer);
    }

}
