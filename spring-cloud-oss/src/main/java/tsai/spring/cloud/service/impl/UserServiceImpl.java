package tsai.spring.cloud.service.impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.tsaiframework.boot.constant.WarningsConstants;
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

}
