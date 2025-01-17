package tsai.spring.cloud.service.impl;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tsai.spring.cloud.service.SecurityLoginService;
/**
 * 自定义登录逻辑
 * @author tsai
 */
@Service
public class SecurityLoginServiceImpl implements SecurityLoginService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!"admin".equals(username)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new User(username, "null", AuthorityUtils.commaSeparatedStringToAuthorityList("normal,ROLE_uu"));
    }
}
