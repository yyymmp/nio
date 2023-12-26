package com.sup.netty.c4.server.handle;

import com.sup.netty.c4.message.RpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月25日 20:50
 */
@Slf4j
public class RpcResponseHandle extends SimpleChannelInboundHandler<RpcResponseMessage> {
    //key: 每个方法被调用是的唯一序号
    //value: 用来接受结果的Promise对象
    public static final Map<Integer, Promise<Object>> promiseMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.error("{}", msg);
        int sequenceId = msg.getSequenceId();
        Promise<Object> promise = promiseMap.get(sequenceId);
        if (promise != null){
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();
            if (exceptionValue != null){
                promise.setFailure(exceptionValue);
            }else {
                promise.setSuccess(msg.getReturnValue());
            }
        }

    }
}
