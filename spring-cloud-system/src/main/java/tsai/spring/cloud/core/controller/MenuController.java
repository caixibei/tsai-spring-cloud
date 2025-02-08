package tsai.spring.cloud.core.controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping(value = "/system/menu")
public class MenuController {

    @PostMapping("/getMenu")
    public String test () {
        return "xxxx";
    }
}
