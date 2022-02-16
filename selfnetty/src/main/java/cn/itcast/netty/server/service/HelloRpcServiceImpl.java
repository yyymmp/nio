package cn.itcast.netty.server.service;

/**
 * @author jlz
 * @date 2022年02月16日 21:42
 */
public class HelloRpcServiceImpl implements HelloRpcService {

    @Override
    public String sayHello(String name) {
        return "你好" + name;
    }
}
