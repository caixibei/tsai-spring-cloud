package tsai.spring.cloud.service;

import com.github.yulichang.base.MPJBaseService;

import tsai.spring.cloud.pojo.User;

/**
 * 用户管理
 * 
 * @author tsai
 */
public interface UserService extends MPJBaseService<User> {

    /**
     * 通过 username 去查找用户信息
     * 
     * @param username 唯一的登录用户名
     * @return 唯一的用户
     */
    User findByUserName(String username);
}
