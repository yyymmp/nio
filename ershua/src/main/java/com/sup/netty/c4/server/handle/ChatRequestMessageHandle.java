package com.sup.netty.c4.server.handle;

import com.sup.netty.c4.message.ChatRequestMessage;
import com.sup.netty.c4.message.ChatResponseMessage;
import com.sup.netty.c4.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月19日 22:38
 */
@Slf4j
@Sharable
public class ChatRequestMessageHandle extends SimpleChannelInboundHandler<ChatRequestMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        //消息接收人
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (null != channel) {
            //对方在线
            ChatResponseMessage chatResponseMessage = new ChatResponseMessage(msg.getFrom(), msg.getContent());
            channel.writeAndFlush(chatResponseMessage);
        }
        //不在线 给发送者发送信息
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户不在线"));

        }
    }
}
