package tsai.spring.cloud.mapper;
import com.tsaiframework.boot.base.mapper.TsaiMapper;
import org.apache.ibatis.annotations.Mapper;
import tsai.spring.cloud.pojo.User;
@Mapper
public interface UserMapper extends TsaiMapper<User> {

    /**
     * 通过 username 去查找用户信息
     * @param username 唯一的登录用户名
     * @return 唯一的用户
     */
    User findByUserName(String username);
}
