package tsai.spring.cloud.controller;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
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

    /**
     * 登录失败处理
     * @param model 模型数据
     * @param request 请求报文
     * @return {@link String}
     */
    @RequestMapping("/failure")
    public String failure(Model model, HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object trace = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE);
        model.addAttribute("status", status);
        model.addAttribute("message", message);
        model.addAttribute("path", path);
        model.addAttribute("exception", exception);
        model.addAttribute("trace", trace);
        return "error/401";
    }
}
