package tsai.spring.cloud.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.tsaiframework.boot.constant.WarningsConstants;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 无权访问处理器
 * 
 * @author tsai
 */
@Component
@SuppressWarnings(WarningsConstants.DUPLICATES)
public class AccessDenyHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e)
        throws IOException, ServletException {
        response.reset();
        response.setContentType("application/json;charset=utf-8");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Allow-Origin", "*");
        JSONObject object = new JSONObject();
        object.putOnce("code", HttpStatus.HTTP_FORBIDDEN);
        object.putOnce("success", false);
        object.putOnce("timestamp", System.currentTimeMillis());
        object.putOnce("message", "权限不足，无法访问！");
        response.getWriter().write(JSONUtil.toJsonStr(object));
        response.flushBuffer();
    }
}
