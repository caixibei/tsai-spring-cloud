package tsai.spring.cloud.handler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // ...自定义登录成功逻辑
        // 获取 UserDetails 实现（可以理解为 User）
        User principal = (User) authentication.getPrincipal();
        String username = principal.getUsername();
        String password = principal.getPassword();//出于安全考虑，输出 Null
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
        System.out.println(username);
        System.out.println(password);
        System.out.println(authorities);
        System.out.println(principal);
        response.sendRedirect("http://127.0.0.1:9000/dashboard.html");
    }
}
