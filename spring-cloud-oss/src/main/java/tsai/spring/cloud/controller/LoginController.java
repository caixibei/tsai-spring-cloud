package tsai.spring.cloud.controller;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.result.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/oauth")
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class LoginController {

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Value("${spring.application.name}")
    private String clientId;

    @GetMapping("/client_detail")
    public ResponseResult<Map<String,String>> loginPage() {
        ClientDetails details = clientDetailsService.loadClientByClientId(clientId);
        Map<String,String> map = new HashMap<>();
        map.put("client_id",details.getClientId());
        map.put("client_secret",details.getClientSecret());
        return ResponseResult.success(map);
    }

}
