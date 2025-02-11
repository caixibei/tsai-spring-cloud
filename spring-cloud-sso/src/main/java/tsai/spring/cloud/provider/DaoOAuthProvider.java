package tsai.spring.cloud.provider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import tsai.spring.cloud.service.impl.UserDetailsServiceImpl;
/**
 * AuthenticationProvider 的作用
 * <p>
 *     ① 验证用户身份： AuthenticationProvider 接口的主要职责是验证用户的身份信息（如用户名和密码）。
 * 它接收一个 Authentication 对象，包含用户提交的认证信息，并返回一个经过验证的 Authentication 对象。
 * </p>
 * <p>
 *     ② 支持多种认证方式：
 * 通过实现不同的 AuthenticationProvider，可以支持多种认证方式，例如用户名/密码认证、OAuth2、JWT、LDAP 等。
 * 可以配置多个 AuthenticationProvider，并按顺序尝试每个提供者进行认证。
 * </p>
 * <p>
 *     ③ 自定义认证逻辑：
 * 开发者可以实现 AuthenticationProvider 接口，定义自定义的认证逻辑，例如从数据库加载用户信息、
 * 调用外部服务进行认证等。
 * </p>
 * <p>
 *     ④ AuthenticationProvider 接口定义了两个方法：
 * </p>
 * <pre>{@code
 *  // 用于验证用户身份
 *  authenticate(Authentication authentication);
 *  // 用于判断当前 AuthenticationProvider 是否支持指定的认证类型
 *  supports(Class<?> authentication);
 * }</pre>
 * <p>注意：如果配置了AuthenticationProvider，就不需要再配置UserDetailsService了。</p>
 */
public class DaoOAuthProvider implements AuthenticationProvider {

    private UserDetailsServiceImpl userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
        authenticationToken.setDetails(authentication.getDetails());
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 确保该 Provider 支持 UsernamePasswordAuthenticationToken 类型的认证
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
