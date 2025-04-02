package tsai.spring.cloud.filter;
import cn.hutool.core.util.StrUtil;
import com.tsaiframework.boot.constant.WarningsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Component
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenStore tokenStore;

    @Override
    @SuppressWarnings(WarningsConstants.UNCHECKED)
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {
        String token = extractToken(request);
        if (StrUtil.isNotBlank(token)) {
            try {
                // 使用 JwtTokenStore 验证 token
                OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
                if (oAuth2AccessToken != null && !oAuth2AccessToken.isExpired()) {
                    // 获取 token 中的额外信息
                    Map<String, Object> additionalInfo = oAuth2AccessToken.getAdditionalInformation();
                    Object object = additionalInfo.get("authorities");
                    if (object instanceof List<?>) {
                        List<String> checkedList = Collections.checkedList(new ArrayList<>(), String.class);
                        checkedList.addAll((List<String>) object);
                        // 创建认证信息
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                additionalInfo.get("username"),
                                null,
                                checkedList.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList())
                        );
                        // 设置认证信息到 SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                // token 验证失败的处理
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/login") ||
                path.startsWith("/error") ||
                path.startsWith("/oauth") ||
                path.startsWith("/captcha") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".jpg") ||
                path.endsWith(".png") ||
                path.endsWith(".gif") ||
                path.endsWith(".ico") ||
                path.endsWith(".mp4") ||
                path.endsWith(".webm") ||
                path.endsWith(".json") ||
                path.endsWith(".ttf") ||
                path.endsWith(".woff") ||
                path.endsWith(".woff2") ||
                path.endsWith("/login.html") ||
                path.endsWith("/error/403.html");
    }

    protected String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
