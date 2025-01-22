package tsai.spring.cloud.mapper;
import com.tsaiframework.boot.base.mapper.TsaiMapper;
import org.apache.ibatis.annotations.Mapper;
import tsai.spring.cloud.pojo.User;
@Mapper
public interface UserMapper extends TsaiMapper<User> {

}
