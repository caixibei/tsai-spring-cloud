package tsai.spring.cloud.handler;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.BranchUtil;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@SuppressWarnings(WarningsConstants.DUPLICATES)
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        JSONObject object = new JSONObject();
        BranchUtil.branchHandler(exception instanceof BadCredentialsException, () -> object.putOnce("details", "账号密码错误，请重新登录！"));
        BranchUtil.branchHandler(exception instanceof AccountExpiredException, () -> object.putOnce("details", "认证已过期，请重新登录！"));
        BranchUtil.branchHandler(exception instanceof AccountStatusException, () -> object.putOnce("details", "账号状态异常，请重新登录！"));
        object.putOnce("code", HttpStatus.HTTP_INTERNAL_ERROR);
        object.putOnce("success", false);
        object.putOnce("timestamp", System.currentTimeMillis());
        object.putOnce("message", "登录失败！");
        response.reset();
        response.setContentType("application/json;charset=utf-8");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().write(JSONUtil.toJsonStr(object));
        response.flushBuffer();
    }
}
