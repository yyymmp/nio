package com.sup.netty.c4.server.handle;

import com.sup.netty.c4.message.LoginRequestMessage;
import com.sup.netty.c4.message.LoginResponseMessage;
import com.sup.netty.c4.server.ChatServer;
import com.sup.netty.c4.server.service.UserService;
import com.sup.netty.c4.server.service.UserServiceMemoryImpl;
import com.sup.netty.c4.server.session.SessionFactory;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月19日 22:36
 */
@Slf4j
@Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        UserService userService = new UserServiceMemoryImpl();
        boolean login = userService.login(username, password);
        LoginResponseMessage responseMessage;
        if (login) {
            log.error("login success");
            responseMessage = new LoginResponseMessage(true, "登录成功");
            //绑定用户与channel的关系
            SessionFactory.getSession().bind(ctx.channel(), username);

        } else {
            log.error("login fail");
            responseMessage = new LoginResponseMessage(false, "登录失败");
        }
        ctx.writeAndFlush(responseMessage);
    }
}
