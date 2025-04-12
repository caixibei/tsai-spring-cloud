package tsai.spring.cloud.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.google.common.annotations.Beta;
import com.tsaiframework.boot.constant.WarningsConstants;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 登录成功处理器
 * 
 * @author tsai
 */
@Beta
@SuppressWarnings({WarningsConstants.DUPLICATES})
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        try {
            // TODO 生成 token 返回
            JSONObject object = new JSONObject();
            object.putOnce("code", HttpStatus.HTTP_OK);
            object.putOnce("success", true);
            object.putOnce("timestamp", System.currentTimeMillis());
            object.putOnce("token", "<token>");
            object.putOnce("user", authentication.getPrincipal());
            response.reset();
            response.setContentType("application/json;charset=utf-8");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.getWriter().write(JSONUtil.toJsonStr(object));
            response.flushBuffer();
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.putOnce("code", HttpStatus.HTTP_INTERNAL_ERROR);
            error.putOnce("success", false);
            error.putOnce("message", "登录失败");
            error.putOnce("timestamp", System.currentTimeMillis());
            response.reset();
            response.setStatus(HttpStatus.HTTP_INTERNAL_ERROR);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSONUtil.toJsonStr(error));
            response.flushBuffer();
        }
    }
}
