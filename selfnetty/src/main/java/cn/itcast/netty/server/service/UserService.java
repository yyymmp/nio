package cn.itcast.netty.server.service;

/**
 * @author jlz
 * @date 2022年01月23日 12:50
 */
public interface UserService {
    /**
     * 登录
     * @author jlz
     * @date 2022/1/23 12:50
     * @param username username
     * @param password password
     * @return boolean
     */
    boolean login(String username, String password);
}
