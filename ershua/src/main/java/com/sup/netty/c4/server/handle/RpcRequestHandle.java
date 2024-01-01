package com.sup.netty.c4.server.handle;

import com.sup.netty.c4.message.RpcRequestMessage;
import com.sup.netty.c4.message.RpcResponseMessage;
import com.sup.netty.c4.server.service.HelloService;
import com.sup.netty.c4.server.service.ServicesFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jlz
 * @date 2023年12月25日 20:46
 */
public class RpcRequestHandle extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        try {
            HelloService service = (HelloService) ServicesFactory.getService(Class.forName(msg.getInterfaceName()));
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(service, msg.getParameterValue());
            System.out.println(invoke);
            rpcResponseMessage.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponseMessage.setExceptionValue(new Exception("远程调用出错" + e.getCause().getMessage()));
        }
        rpcResponseMessage.setSequenceId(msg.getSequenceId());
        ctx.writeAndFlush(rpcResponseMessage);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(1,
                "com.sup.netty.c4.server.service.HelloService"
                , "sayHello"
                , String.class
                , new Class[]{java.lang.String.class}
                , new Object[]{"zhangsan"});

        HelloService service = (HelloService) ServicesFactory.getService(Class.forName(rpcRequestMessage.getInterfaceName()));
        Method method = service.getClass().getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParameterTypes());
        Object invoke = method.invoke(service, rpcRequestMessage.getParameterValue());
        System.out.println(invoke);

    }
}
