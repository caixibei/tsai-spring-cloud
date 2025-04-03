package tsai.spring.cloud.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

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

    @PostMapping("/error")
    public ModelAndView error(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        // 从request属性获取错误信息
        if (request.getAttribute("error") != null) {
            mav.addObject("error", request.getAttribute("error"));
        }
        mav.setViewName("login");
        return mav;
    }
}
