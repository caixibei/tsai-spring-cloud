package tsai.spring.cloud.handler;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void exception(Exception e, HttpServletResponse response) throws Exception{
        handleException(e, response, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public void exception(RuntimeException e, HttpServletResponse response) throws Exception{
        handleException(e, response, e.getMessage());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public void exception(InternalAuthenticationServiceException e, HttpServletResponse response) throws Exception{
        handleException(e, response, e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public void invocationTargetException(Exception e, HttpServletResponse response) throws Exception{
        handleException(e, response,"操作异常,请检查数据是否合法");
    }

    private void handleException(Exception e,HttpServletResponse response,String title) throws IOException {
        response.reset();
        response.setContentType("application/json;charset=utf-8");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Allow-Origin", "*");
        JSONObject object = new JSONObject();
        object.putOnce("code", HttpStatus.HTTP_INTERNAL_ERROR);
        object.putOnce("success", false);
        object.putOnce("message", title);
        object.putOnce("details", e.getMessage());
        object.putOnce("timestamp", System.currentTimeMillis());
        response.getWriter().write(JSONUtil.toJsonStr(object));
    }
}
