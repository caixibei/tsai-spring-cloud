package tsai.spring.cloud.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletResponse;
/**
 * 登录跳转处理器
 * @author tsai
 */
@Controller
public class LoginRedirectController {
    /**
     * 登录成功跳转
     * @return 跳转地址
     */
    @PostMapping("/index")
    public String dashboard() {
        return "redirect:/index.html?target=http://localhost:7090/index.html";
    }

    /**
     * 登录失败跳转
     * @return 跳转地址
     */
    @PostMapping("/error")
    public String error(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "redirect:/error/403.html";
    }
}
