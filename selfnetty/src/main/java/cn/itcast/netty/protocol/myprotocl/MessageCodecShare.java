package cn.itcast.netty.protocol.myprotocl;

import cn.itcast.netty.config.Config;
import cn.itcast.netty.protocol.myprotocl.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToMessageCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 必须要帧解码器一起使用 数据帧完整
 * @author jlz
 * @date 2022年01月22日 18:08
 */
@Sharable
@Slf4j
public class MessageCodecShare extends MessageToMessageCodec<ByteBuf,Message> {
    //这两个泛型是为了ByteBuf Message
    //入站 ByteBuf 出战 Message
    // 经过帧解码器解码之后 获得一个完整帧的ByteBuf
    // 该ByteBuf必然可以转化位一个Message对象
    // INBOUND_IN: ByteBuf , OUTBOUND_IN: Message
    // 出战需要encode 签名 msg
    // 入站需要decode 签名:ByteBuf 将ByteBuf进行解码

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> list) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1 字节的版本,
        out.writeByte(1);
        // 3. 1 字节的序列化方式 jdk 0 , json 1
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 4. 1 字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5. 4 个字节
        out.writeInt(msg.getSequenceId());
        // 无意义，对齐填充
        out.writeByte(0xff);
        // 6. 获取内容的字节数组
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        list.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerAlgorithm = in.readByte(); // 0 或 1
        byte messageType = in.readByte(); // 0,1,2...
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 找到反序列化算法
        Serialize.Algorithm algorithm = Serialize.Algorithm.values()[serializerAlgorithm];
        // 确定具体消息类型
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = algorithm.deserialize(messageClass, bytes);
        log.debug("++++++++++++{}, {}, {}, {}, {}, {}", magicNum, version, serializerAlgorithm, messageType, sequenceId, length);
        log.debug("++++++++++++{}", message);
        out.add(message);
    }

}
