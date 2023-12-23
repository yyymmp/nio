package com.sup.netty.c4.server.handle;

import com.sup.netty.c4.server.session.SessionFactory;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月23日 18:29
 */
@Slf4j
@Sharable
public class QuitHandle extends ChannelInboundHandlerAdapter {

    /**
     * 连接断开时会触发  处理客户端正常断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.error("{} 已经断开", ctx.channel());
    }

    /**
     * 连接异常断开
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.error("{} 异常断开:{}", ctx.channel(),cause);
    }
}
