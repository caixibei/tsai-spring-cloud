package tsai.spring.cloud.handler;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.google.common.annotations.Beta;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.BranchUtil;

import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 登录失败处理器
 * 
 * @author caixb
 */
@Component
@SuppressWarnings(WarningsConstants.DUPLICATES)
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
        // 保存错误信息到request属性
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 401);
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, exception.getMessage());
        request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());
        request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, exception);
        request.setAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE, exception.getClass().getName());
        // 具体错误信息处理
        BranchUtil.branchHandler(exception instanceof BadCredentialsException,
            () -> request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "账号密码错误，请检查后重试!"));
        BranchUtil.branchHandler(exception instanceof AccountExpiredException,
            () -> request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "认证信息已过期，请重新登录!"));
        BranchUtil.branchHandler(exception instanceof AccountStatusException,
            () -> request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "账号状态异常，请联系管理员处理!"));
        // 转发而不是重定向，以保留request属性
        request.getRequestDispatcher("/failure").forward(request, response);
    }

    /**
     * 针对现代 SPI 单页应用程序，往往喜欢返回 JSON 错误信息
     * 
     * @param request 请求报文
     * @param response 响应报文
     * @param exception 异常信息
     * @throws IOException 异常
     */
    @Beta
    protected void failureJsonMessage(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException {
        JSONObject object = new JSONObject();
        object.putOpt("details", exception.getMessage());
        BranchUtil.branchHandler(exception instanceof BadCredentialsException,
            () -> object.putOpt("details", "账号密码错误，请重新登录！"));
        BranchUtil.branchHandler(exception instanceof AccountExpiredException,
            () -> object.putOpt("details", "认证已过期，请重新登录！"));
        BranchUtil.branchHandler(exception instanceof AccountStatusException,
            () -> object.putOpt("details", "账号状态异常，请重新登录！"));
        object.putOpt("code", HttpStatus.HTTP_INTERNAL_ERROR);
        object.putOpt("success", false);
        object.putOpt("timestamp", System.currentTimeMillis());
        object.putOpt("message", "登录失败");
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
