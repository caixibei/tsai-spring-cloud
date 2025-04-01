package tsai.spring.cloud.strategy;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tsaiframework.boot.constant.WarningsConstants;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;
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
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        HttpServletResponse response = getHttpServletResponse(event);
        JSONObject object = new JSONObject();
        object.putOnce("code", HttpStatus.HTTP_FORBIDDEN);
        object.putOnce("success", false);
        object.putOnce("timestamp", System.currentTimeMillis());
        object.putOnce("message", "您的账号在别处登录，当前登录失效！");
        response.getWriter().write(JSONUtil.toJsonStr(object));
        response.flushBuffer();
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
