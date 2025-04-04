package tsai.spring.cloud.controller;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletRequest;
/**
 * 登录跳转处理器
 *
 * @author tsai
 */
@Controller
public class LoginRedirectController {
    /**
     * 登录成功重定向首页
     *
     * @return 跳转地址
     */
    @PostMapping("/home")
    public String dashboard() {
        return "redirect:view/home/home.html";
    }
}
