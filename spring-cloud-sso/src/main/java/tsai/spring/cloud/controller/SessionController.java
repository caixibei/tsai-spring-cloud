package tsai.spring.cloud.controller;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tsai.spring.cloud.service.SessionService;

import java.io.IOException;

@RestController
@RequestMapping(value = "/session")
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @GetMapping(value="/online")
    public ResponseResult<?> online() throws IOException {
        return ResponseResult.success(sessionService.getOnlineUsers());
    }
}
