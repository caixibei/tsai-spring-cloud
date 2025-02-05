package tsai.spring.cloud.strategy;
import com.tsaiframework.boot.result.ResponseResult;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class SessionExpiredStrategy implements InvalidSessionStrategy {

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        cancelCookie(request, response);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(ResponseResult.error("您的账户在别处登录，当前登录失效！").getMessage());
    }

    protected void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath(getCookiePath(request));
        response.addCookie(cookie);
    }

    protected String getCookiePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return !contextPath.isEmpty() ? contextPath : "/";
    }
}
