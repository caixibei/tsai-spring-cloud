package tsai.spring.cloud.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
/**
 * 登录跳转处理器
 * @author tsai
 */
@Controller
public class LoginRedirectController {
    /**
     * 登录成功重定向首页
     * @return 跳转地址
     */
    @PostMapping("/index")
    public String dashboard() {
        return "redirect:/index.html";
    }

    @PostMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }
}
