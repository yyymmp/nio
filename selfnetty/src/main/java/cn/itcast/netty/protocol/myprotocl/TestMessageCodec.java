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
        //channel.writeOutbound(message);

        //测试解码器 所以入站   入战编码
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        MessageCodec messageCodec = new MessageCodec();
        //将消息写入buffer
        messageCodec.encode(null,message,buffer);
        //将encode后的消息传入 观察是否能正确解码
        //channel.writeInbound(buffer);

        //验证自定义协议为何还要使用帧解码器
        ByteBuf s1 = buffer.slice(0, 100);
        //使用不完成的半包,如果不配置帧解码器 那么就会出现异常  java.lang.IndexOutOfBoundsException
        //读到长度 却没有对应长度的字节
        //配置帧解码器,在帧解码器这一层 没有收集到一个完成的帧 数据都不会走到下一步handle
        //更不会走到编解码器整理,不会因为半包问题而解析四百
        channel.writeInbound(s1);
    }

}
