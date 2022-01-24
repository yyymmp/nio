package cn.itcast.netty.protocol.myprotocl;

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
        //魔数 4
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //协议版本 1
        out.writeByte(1);
        // 字节的序列化方式 jdk 0 , json 1 1
        out.writeByte(0);
        //报文类型 1
        out.writeByte(msg.getMessageType());
        //顺序 1
        out.writeInt(msg.getSequenceId());
        //字节填充 一个字节 1
        out.writeByte(0xff);
        //对象转字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        //长度  4
        out.writeInt(bytes.length);
        //内容 长度未定
        out.writeBytes(bytes);

        //给下一个出战处理器
        list.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
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
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        log.debug("-----{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("-----{}", message);
        //解码后的消息
        //交给其他入站处理器
        out.add(message);
    }

}
