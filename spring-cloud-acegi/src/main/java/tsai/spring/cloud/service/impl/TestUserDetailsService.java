package tsai.spring.cloud.service.impl;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class TestUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("执行自定义登录逻辑...");
        try {
            // 模拟从数据库根据 username 获取用户
            if (!"admin".equals(username)) {
                throw new UsernameNotFoundException("用户不存在");
            }
            // 比对密码（用户注册的时候，密码肯定是密文的），如果匹配成功返回 UserDetails
            return new User(username, "123", AuthorityUtils.commaSeparatedStringToAuthorityList("admin,normal"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return new User(username, "null",AuthorityUtils.commaSeparatedStringToAuthorityList("normal"));
    }
}
