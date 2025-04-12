package tsai.spring.cloud.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.tsaiframework.boot.base.mapper.TsaiMapper;

import tsai.spring.cloud.pojo.User;

/**
 * 用户管理
 * 
 * @author caixb
 */
@Mapper
public interface UserMapper extends TsaiMapper<User> {

    /**
     * 通过 username 去查找用户信息
     * 
     * @param username 唯一的登录用户名
     * @return 唯一的用户
     */
    User findByUserName(String username);
}
