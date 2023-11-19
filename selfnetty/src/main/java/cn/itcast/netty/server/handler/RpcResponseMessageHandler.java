package cn.itcast.netty.server.handler;

import cn.itcast.netty.protocol.myprotocl.message.RpcRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.RpcResponseMessage;
import cn.itcast.netty.server.service.HelloRpcService;
import cn.itcast.netty.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2022年02月16日 21:30
 */
@Slf4j
@Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    public static final Map<Integer, Promise<Object>> promiseMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage message) throws Exception {
        log.info("client: 收到RpcResponseMessage消息");
        //获取到空的promise
        Promise<Object> promise = promiseMap.remove(message.getSequenceId());
        //将响应结果放到promise中  在调用出就可以拿到该结果
        if (promise != null){
            if (message.getExceptionValue()!=null) {
                //异常
                promise.setFailure(message.getExceptionValue());
            }else {
                promise.setSuccess(message.getReturnValue());
            }
        }

    }



    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(
                1,
                "cn.itcast.netty.server.service.HelloRpcService",
                "sayHello",
                String.class,
                //参数类型
                new Class[]{String.class},
                //参数值
                new Object[]{"张三"}
        );

        //根据接口类型找到实现类对象
        HelloRpcService service = (HelloRpcService)ServicesFactory.getService(Class.forName(message.getInterfaceName()));
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        System.out.println(method.invoke(service, message.getParameterValue()));
    }
}
