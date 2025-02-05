package tsai.spring.cloud.service.impl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import tsai.spring.cloud.service.PermissionService;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
/**
 * 基于 access 权限访问控制
 * @author tsai
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Collection<GrantedAuthority> authorities = user.getAuthorities();
        // 判断请求的URI是否在请求的权限中
        return authorities.contains(new SimpleGrantedAuthority(request.getRequestURI()));
    }

}
