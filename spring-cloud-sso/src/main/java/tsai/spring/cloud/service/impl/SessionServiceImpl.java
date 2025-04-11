package tsai.spring.cloud.service.impl;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import tsai.spring.cloud.service.SessionService;
import java.util.Map;
@Slf4j
@Service
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class SessionServiceImpl implements SessionService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Override
    public UserDetails getOnlineUsers() {
        Map<String, ? extends Session> sessionMap = sessionRepository.findByPrincipalName("*");
        return sessionMap.values().stream()
                .findFirst()
                .map(session -> {
                    SecurityContext context = session.getAttribute("SPRING_SECURITY_CONTEXT");
                    return (context != null) ? (UserDetails) context.getAuthentication().getPrincipal() : null;
                })
                .orElse(null);
    }
}
