package tsai.spring.cloud.controller;
import org.springframework.web.bind.annotation.PostMapping;
@Deprecated
public class RedirectController {
    @PostMapping("/dashboard")
    public String dashboard () {
        return "redirect:dashboard.html";
    }

    @PostMapping("/error")
    public String error () {
        return "redirect:error.html";
    }
}
