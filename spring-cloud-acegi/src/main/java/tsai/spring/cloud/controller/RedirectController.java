package tsai.spring.cloud.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class RedirectController {
    @PostMapping("/dashboard")
    public String dashboard () {
        return "redirect:https://docs.spring.io";
    }

    @PostMapping("/error")
    public String error () {
        return "redirect:https://element-plus.org/";
    }
}
