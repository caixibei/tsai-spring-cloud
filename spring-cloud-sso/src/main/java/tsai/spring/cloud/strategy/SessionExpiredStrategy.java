package tsai.spring.cloud.strategy;
import com.tsaiframework.boot.constant.WarningsConstants;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * 多端登录Session过期策略
 * @author tsai
 */
@Component
@SuppressWarnings(WarningsConstants.DUPLICATES)
public class SessionExpiredStrategy implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        // TODO 无状态的登录使用
        // HttpServletResponse response = getHttpServletResponse(event);
        // JSONObject object = new JSONObject();
        // object.putOnce("code", HttpStatus.HTTP_FORBIDDEN);
        // object.putOnce("success", false);
        // object.putOnce("timestamp", System.currentTimeMillis());
        // object.putOnce("message", "您的账号在别处登录，当前登录失效！");
        // response.getWriter().write(JSONUtil.toJsonStr(object));
        // response.flushBuffer();
        // session 会话管理建议跳转页面
        // 保存错误信息到request属性
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = getHttpServletResponse(event);
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 401);
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "您的账号在别处登录，当前登录失效！");
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());
        // 转发而不是重定向，以保留request属性
        request.getRequestDispatcher("/expired").forward(request, response);
    }

    protected static HttpServletResponse getHttpServletResponse(SessionInformationExpiredEvent event) {
        HttpServletResponse response = event.getResponse();
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
