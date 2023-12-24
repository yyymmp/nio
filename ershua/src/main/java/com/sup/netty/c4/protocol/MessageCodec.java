package com.sup.netty.c4.protocol;

import com.sup.netty.c4.config.Config;
import com.sup.netty.c4.message.Message;
import com.sup.netty.c4.protocol.Serializer.Algorithm;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月14日 22:19
 */
@Slf4j
@Sharable
public class MessageCodec extends ByteToMessageCodec<Message> {


    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //出战编码
        //1 魔数 约定4字节1234
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //2 版本 1字节
        out.writeByte(1);
        //3 序列化算法 0 =>jdk方式 1=>json方式  1字节
        log.error("Config.getSerializerAlgorithm():{}",Config.getSerializerAlgorithm().name());
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        //4 指令类型 运行时子类对象实现父类方法获取 1字节
        out.writeByte(msg.getMessageType());
        //5 请求序号  4字节
        out.writeInt(msg.getSequenceId());
        //对其字节  无意义 1字节
        out.writeByte(0xff);
        //6 长度与内容  消息体在msg对象中 需要将对象转为字节数组
        byte[] bytes1 = Config.getSerializerAlgorithm().serialize(msg);
        //长度 4字节
        out.writeInt(bytes1.length);
        //内容
        out.writeBytes(bytes1);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //出战解码
        //1 魔数 读取4字节
        int magicNum = in.readInt();
        //2 版本 读取1字节
        byte version = in.readByte();
        //3 序列化方式 1字节
        byte serType = in.readByte();
        //4 指令类型 1字节
        byte messType = in.readByte();
        //5 序列号
        int sequenceId = in.readInt();
        // 6 无意义字节
        in.readByte();
        // 7 长度
        int len = in.readInt();
        byte[] bytes = new byte[len];
        //ByteBuf byteBuf = in.readBytes(len);
        ByteBuf byteBuf = in.readBytes(len);
        byteBuf.readBytes(bytes);
        //in.readBytes(bytes, 0, len);
        //反序列化内容
        Algorithm algorithm = Algorithm.values()[serType];
        //这里需要具体的消息类型 因为提前做了映射关系 因为如果是json序列化方式
        Message message = algorithm.deserialize(Message.getMessageClass(messType), bytes);

        log.error("==========={}",message.getClass());
        log.error("{},{},{},{},{}", magicNum, version, serType, messType, sequenceId);
        log.error("{}", message);

        //给下一个handle使用
        out.add(message);
    }
}
