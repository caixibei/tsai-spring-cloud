package tsai.spring.cloud.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class LoginRedirectController {
    /**
     * 登录成功跳转
     * @return 跳转地址
     */
    @PostMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard.html";
    }

    /**
     * 登录失败跳转
     * @return 跳转地址
     */
    @PostMapping("/error")
    public String error() {
        return "redirect:/error.html";
    }
}
