package cn.itcast.netty.protocol.myprotocl;

import cn.itcast.netty.config.Config;
import cn.itcast.netty.protocol.myprotocl.Serialize.Algorithm;
import cn.itcast.netty.protocol.myprotocl.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 编解码器
 *
 * @author jlz
 * @date 2022年01月19日 11:26
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    /**
     * 入站解码
     *
     * @param ctx
     * @param in
     * @param out
     * @author jlz
     * @date 2022/1/19 14:00
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        //魔数 4字节
        int magicNum = in.readInt();
        //协议版本
        byte version = in.readByte();
        //序列化方式 假设都是jdk序列化方式
        byte serializerType = in.readByte();
        //消息类型
        byte messageType = in.readByte();
        //顺序
        int sequenceId = in.readInt();
        //填充字节 忽略
        in.readByte();
        //内容长度
        int length = in.readInt();
        byte[] bytes = new byte[length];
        //消息内容
        in.readBytes(bytes, 0, length);
        //字节转对象
        //根据消息类型找到对应的class 不能使用父类
        Algorithm algorithm = Algorithm.values()[serializerType];
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = algorithm.deserialize(messageClass, bytes);
        log.debug("-----{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("-----{}", message);
        //解码后的消息
        out.add(message);
    }

    /**
     * 出站编码
     *
     * @param ctx
     * @param msg
     * @param out
     * @author jlz
     * @date 2022/1/19 11:32
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //魔数 4
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //协议版本 1
        out.writeByte(1);
        // 字节的序列化方式 jdk 0 , json 1 1
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        //报文类型 1
        out.writeByte(msg.getMessageType());
        //顺序 1
        out.writeInt(msg.getSequenceId());
        //字节填充 一个字节 1
        out.writeByte(0xff);
        //对象转字节数组
        byte[] bytes = Algorithm.values()[Config.getSerializerAlgorithm().ordinal()].serialize(msg);
        //长度  4
        out.writeInt(bytes.length);
        //内容 长度未定
        out.writeBytes(bytes);
    }
}
