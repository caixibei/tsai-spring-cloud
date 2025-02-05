package tsai.spring.cloud.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tsai.spring.cloud.mapper.UserMapper;
import tsai.spring.cloud.pojo.User;
import tsai.spring.cloud.service.UserService;
@Service
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class UserServiceImpl extends MPJBaseServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 通过 username 去查找用户信息
     * @param username 唯一的登录用户名
     * @return 唯一的用户
     */
    public User findByUserName(String username) {
        ExceptionUtil.throwException(StrUtil.isBlank(username),"username 不可为空，查询参数无效。");
        return userMapper.findByUserName(username);
    }
}
