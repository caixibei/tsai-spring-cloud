package tsai.spring.cloud.controller;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("permissionServiceImpl.hasPermission(request,authentication)")
    public String hello(){
        return "Hello World!";
    }

}
