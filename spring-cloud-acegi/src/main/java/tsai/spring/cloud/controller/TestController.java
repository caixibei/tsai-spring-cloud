package tsai.spring.cloud.controller;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    @Secured("ROEL_admin")
    public String hello(){
        return "Hello World!";
    }

}
