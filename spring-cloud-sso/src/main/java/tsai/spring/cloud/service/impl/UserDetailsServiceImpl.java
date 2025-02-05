package tsai.spring.cloud.service.impl;
import com.tsaiframework.boot.constant.WarningsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tsai.spring.cloud.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * Security 自定义登录逻辑
 * @author tsai
 */
@Service
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        tsai.spring.cloud.pojo.User user = userService.findByUserName(username);
        return new User(user.getUsername(),user.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
