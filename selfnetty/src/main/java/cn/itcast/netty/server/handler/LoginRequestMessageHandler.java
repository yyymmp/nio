package cn.itcast.netty.server.handler;

import cn.itcast.netty.protocol.myprotocl.message.LoginRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.LoginResponseMessage;
import cn.itcast.netty.server.service.UserServiceFactory;
import cn.itcast.netty.server.session.SessionFactory;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author jlz
 * @date 2022年01月25日 23:06
 */
//没有状态信息  可设置为Sharable
@Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage message;
        if (login) {
            //成功返回消息
            message = new LoginResponseMessage(login, "登录成功");
            //保存用户与channel关系
            SessionFactory.getSession().bind(ctx.channel(), username);
        } else {
            message = new LoginResponseMessage(false, "登录失败");
        }
        //服务端的消息路径 在入站处理器种写入 触发出战处理器 ->messageCodec->loggingHandler
        ctx.writeAndFlush(message);
    }
}
