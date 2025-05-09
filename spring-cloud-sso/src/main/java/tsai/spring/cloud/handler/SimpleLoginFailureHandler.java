package tsai.spring.cloud.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.google.common.annotations.Beta;

/**
 * 简易的登录失败处理器
 * 
 * @author tsai
 */
@Beta
public class SimpleLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private String defaultFailureUrl;

    public SimpleLoginFailureHandler(String defaultFailureUrl) {
        super(defaultFailureUrl);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        // 保存错误信息到request属性
        request.setAttribute("error", exception.getMessage());
        // 转发而不是重定向，以保留request属性
        request.getRequestDispatcher(defaultFailureUrl).forward(request, response);
    }
}
