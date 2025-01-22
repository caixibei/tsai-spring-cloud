package tsai.spring.cloud.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class OssController {

    @PostMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashbord.html";
    }

    @PostMapping("/error")
    public String error() {
        return "redirect:/error.html";
    }
}
