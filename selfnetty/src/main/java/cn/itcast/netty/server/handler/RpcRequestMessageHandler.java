package cn.itcast.netty.server.handler;

import cn.itcast.netty.protocol.myprotocl.message.RpcRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.RpcResponseMessage;
import cn.itcast.netty.server.service.HelloRpcService;
import cn.itcast.netty.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2022年02月16日 21:18
 */
@Sharable
@Slf4j
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        log.info("server: 响应rpc请求");
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        rpcResponseMessage.setSequenceId(message.getSequenceId());
        try {
            //服务端根据接口全限定类名找到在服务端的实现
            log.info("全限定类名{}",message.getInterfaceName());
            HelloRpcService service = (HelloRpcService) ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            //根据方法名 参数类型找到Method对象
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            //传入参数 调用method对象
            Object invoke = method.invoke(service, message.getParameterValue());
            //设置返回值
            rpcResponseMessage.setReturnValue(invoke);
        }catch (Exception e){
            e.printStackTrace();
            rpcResponseMessage.setExceptionValue(new Exception("远程调用出错:"+e.getMessage()));
        }

        ctx.writeAndFlush(rpcResponseMessage);
    }
}
