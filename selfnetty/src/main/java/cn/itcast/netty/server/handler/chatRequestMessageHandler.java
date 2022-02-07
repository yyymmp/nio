package cn.itcast.netty.server.handler;

import cn.itcast.netty.protocol.myprotocl.message.ChatRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.ChatResponseMessage;
import cn.itcast.netty.protocol.myprotocl.message.LoginRequestMessage;
import cn.itcast.netty.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author jlz
 * @date 2022年01月25日 23:19
 */
//只关注某一种具体类型的消息 可以继承Simple
@Sharable
public class chatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        //根据名称获取channel
        Channel channel = SessionFactory.getSession().getChannel(to);
        //在线
        if (channel != null){
            ChatResponseMessage chatResponseMessage = new ChatResponseMessage(msg.getFrom(), msg.getContent());
            channel.writeAndFlush(chatResponseMessage);
        }
        //不在线
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方未上线"));
        }


    }
}
