package tsai.spring.cloud.service.impl;
import cn.hutool.core.util.StrUtil;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.BranchUtil;
import com.tsaiframework.boot.util.ExceptionUtil;
import com.tsaiframework.boot.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tsai.spring.cloud.service.UserService;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
/**
 * Security 自定义登录逻辑
 *
 * @author tsai
 */
@Service
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        // 判断输入的用户名是否为空
        BranchUtil.branchHandler(StrUtil.isNotBlank(username), () -> {
            String captcha = request.getParameter("captcha");
            String key = "captcha_" + username;
            BranchUtil.branchHandler(redisUtil.hasKey(key), () -> {
                String verifyCode = redisUtil.get(key);
                ExceptionUtil.throwException(!StrUtil.equals(verifyCode, captcha), "验证码错误，请重新输入！");
            }, () -> {
                throw new RuntimeException("无效的验证码，请重新生成验证码！");
            });
        }, () -> {
            throw new UsernameNotFoundException("您所输入的用户名不存在！");
        });
        tsai.spring.cloud.pojo.User user = userService.findByUserName(username);
        return new User(user.getUsername(), user.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }

}
