package tsai.spring.cloud.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
public class LoginController {
    private OAuth2RestTemplate oauth2RestTemplate;

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/index.html";
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // 在这里你可以将用户名和密码传递给 OAuth2 的 Token 端点获取 access token
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", username);
        params.add("password", password);
        params.add("client_id", "your-client-id");
        params.add("client_secret", "your-client-secret");
        // 使用 OAuth2RestTemplate 获取 Token
        OAuth2AccessToken accessToken = oauth2RestTemplate.postForObject("http://localhost:8080/oauth/token", params, OAuth2AccessToken.class);
        // 将 Token 存储到 session 或者其他地方
        // 你可以根据 Token 的有效性来决定重定向到哪
        return "redirect:/home";  // 登录成功后跳转
    }
}
