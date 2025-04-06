package tsai.spring.cloud.service.impl;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.annotations.Beta;
import com.tsaiframework.boot.constant.WarningsConstants;
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
import tsai.spring.cloud.constant.RedisConstant;
import tsai.spring.cloud.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Security 自定义登录逻辑
 *
 * @author tsai
 */
@Service
@SuppressWarnings({WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION, WarningsConstants.DUPLICATES})
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // 获取并校验验证码签名
        String uuid = (String) request.getSession().getAttribute("uuid");
        ExceptionUtil.throwException(StrUtil.isBlank(uuid), "验证码签名检验异常，请重新发送验证码!");
        // 根据签名生成验证码标识 key
        String captcha = request.getParameter("captcha");
        String key = StrUtil.concat(false, RedisConstant.CAPTCHA_KEY_PREFIX, uuid);
        // 判断输入的用户名是否为空
        ExceptionUtil.throwException(StrUtil.isBlank(username), "【用户名为空】请输入用户名后重新发送验证码!");
        // 判断验证码是否已经过期
        ExceptionUtil.throwException(!redisUtil.hasKey(key), "【无效验证码】验证码已过期，请重新发送验证码!");
        // 校验验证码是否一致
        String verifyCode = redisUtil.get(key);
        ExceptionUtil.throwException(!StrUtil.equals(verifyCode, captcha), "【验证码错误】验证码输入错误，请检查验证码是否正确!");
        // 从用户信息表中检索用户
        tsai.spring.cloud.pojo.User user = userService.findByUserName(username);
        // 校验用户是否存在
        ExceptionUtil.throwException(ObjUtil.isNull(user), "【用户不存在】系统内无此用户，请检查用户名是否正确!");
        // 删除验证码，确保验证码只能使用一次
        redisUtil.delete(key);
        return new User(user.getUsername(), user.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }

    /**
     * 获取HttpServletResponse
     *
     * @return {@link HttpServletResponse}
     */
    @Beta
    protected static HttpServletResponse getHttpServletResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        assert response != null;
        response.reset();
        response.setContentType("application/json;charset=utf-8");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

}
