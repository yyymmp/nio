package cn.itcast.netty.server.handler;

import cn.itcast.netty.protocol.myprotocl.message.RpcRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.RpcResponseMessage;
import cn.itcast.netty.server.service.HelloRpcService;
import cn.itcast.netty.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;

/**
 * @author jlz
 * @date 2022年02月16日 21:18
 */
@Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        try {
            HelloRpcService service = (HelloRpcService) ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());

            rpcResponseMessage.setReturnValue(invoke);
        }catch (Exception e){
            e.printStackTrace();
            rpcResponseMessage.setExceptionValue(e);
        }

        ctx.writeAndFlush(rpcResponseMessage);
    }
}
