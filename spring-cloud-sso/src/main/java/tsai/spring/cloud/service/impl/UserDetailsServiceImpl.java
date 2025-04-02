package tsai.spring.cloud.service.impl;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.BranchUtil;
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
import java.io.IOException;
import java.util.Objects;
/**
 * Security 自定义登录逻辑
 *
 * @author tsai
 */
@Service
@SuppressWarnings({WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION,WarningsConstants.DUPLICATES})
public class UserDetailsServiceImpl implements  UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpServletResponse response = getHttpServletResponse();
        JSONObject object = new JSONObject();
        object.putOnce("code", HttpStatus.HTTP_INTERNAL_ERROR);
        object.putOnce("success", false);
        object.putOnce("timestamp", System.currentTimeMillis());
        String uuid = (String) request.getSession().getAttribute("uuid");
        if (StrUtil.isBlank(uuid)) {
             uuid = request.getParameter("uuid");
        };
        String captcha = request.getParameter("captcha");
        String key = StrUtil.concat(false, RedisConstant.CAPTCHA_KEY_PREFIX, uuid);
        // 判断输入的用户名是否为空
        BranchUtil.branchHandler(StrUtil.isNotBlank(username), () -> {
            BranchUtil.branchHandler(redisUtil.hasKey(key), () -> {
                String verifyCode = redisUtil.get(key);
                BranchUtil.branchHandler(!StrUtil.equals(verifyCode, captcha),()->{
                    object.putOnce("message", "登录失败！");
                    object.putOnce("details", "验证码错误，请重新输入！");
                    try {
                        response.getWriter().write(JSONUtil.toJsonStr(object));
                        response.flushBuffer();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }, () -> {
                object.putOnce("message", "登录失败！");
                object.putOnce("details", "验证码已过期，请重新生成验证码！");
                try {
                    response.getWriter().write(JSONUtil.toJsonStr(object));
                    response.flushBuffer();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }, () -> {
            object.putOnce("message", "登录失败！");
            object.putOnce("details", "请输入用户名！");
            try {
                response.getWriter().write(JSONUtil.toJsonStr(object));
                response.flushBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // 查询用户
        tsai.spring.cloud.pojo.User user = userService.findByUserName(username);
        BranchUtil.branchHandler(ObjectUtil.isNull(user),()->{
            object.putOnce("message", "登录失败！");
            object.putOnce("details", "您所输入的用户名不存在！");
            try {
                response.getWriter().write(JSONUtil.toJsonStr(object));
                response.flushBuffer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // 删除验证码
        redisUtil.delete(key);
        return new User(user.getUsername(), user.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }

    /**
     * 获取HttpServletResponse
     * @return {@link HttpServletResponse}
     */
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
