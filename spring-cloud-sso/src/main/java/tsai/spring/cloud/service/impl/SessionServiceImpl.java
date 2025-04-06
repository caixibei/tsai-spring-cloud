package tsai.spring.cloud.service.impl;
import cn.hutool.core.util.ObjUtil;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.stereotype.Service;
import tsai.spring.cloud.constant.RedisConstant;
import tsai.spring.cloud.service.SessionService;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class SessionServiceImpl implements SessionService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Object> getOnlineUsers (){
        Set<String> keys = redisUtil.keys(RedisConstant.SPRING_SESSION_PREFIX+"*");
        List<Object> onlineUsers = new ArrayList<>();
        for (String key : keys) {
            if (key.contains("expir")) {
                continue;
            }
            // 获取所有数据
            Map<Object, Object> map = redisUtil.hGetAll(key);
            // 获取sessionId
            String sessionId = key.replace(RedisConstant.SPRING_SESSION_PREFIX+":sessions:", "");
            long maxInactiveIntervalTime = ((Integer) map.get("maxInactiveInterval")).longValue();
            Duration maxInactiveInterval = Duration.ofSeconds(maxInactiveIntervalTime);
            // 判断是否已经过期了这个session
            long lastAccessedTime = (Long) map.get("lastAccessedTime");
            if (Instant.now().minus(maxInactiveInterval)
                    .compareTo(Instant.ofEpochMilli(lastAccessedTime)) >= 0) {
                continue;
            }
            SecurityContextImpl securityContexts = (SecurityContextImpl) map.get("sessionAttr:SPRING_SECURITY_CONTEXT");
            if (ObjUtil.isNull(securityContexts)) {
                continue;
            }
            // 层级比较多需要一层一层拿出来
            Authentication authentication = securityContexts.getAuthentication();
            if (ObjUtil.isNull(authentication)) {
                continue;
            }
            Object principal = authentication.getPrincipal();
            onlineUsers.add(principal);
        }
        return onlineUsers;
    }
}
