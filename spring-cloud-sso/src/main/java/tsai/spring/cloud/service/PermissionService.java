package tsai.spring.cloud.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

/**
 * 权限管理
 * 
 * @author tsai
 */
public interface PermissionService {
    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
