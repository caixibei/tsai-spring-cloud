package tsai.spring.cloud.service.impl;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Service;
import tsai.spring.cloud.constant.RedisConstant;
import tsai.spring.cloud.service.SessionService;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Slf4j
@Service
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class SessionServiceImpl implements SessionService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public List<Object> getOnlineUsers() {
        Set<String> keys = redisUtil.keys(RedisConstant.SPRING_SESSION_PREFIX + "*");
        List<Object> onlineUsers = new ArrayList<>();
        for (String key : keys) {
            if (!StrUtil.startWith(key, RedisConstant.SPRING_SESSION_PREFIX + ":sessions:")
                || StrUtil.contains(key,"expires")) {
                continue;
            }
            // 使用 RedisTemplate 直接获取数据
            Map<Object, Object> map = redisUtil.hGetAll(key);
            if (ObjUtil.isNull(map)) {
                continue;
            }
            // 获取 sessionId
            String sessionId = key.replace(RedisConstant.SPRING_SESSION_PREFIX + ":sessions:", "");
            // 获取 maxInactiveInterval
            Object maxInactiveIntervalObj = map.get("maxInactiveInterval");
            if (ObjUtil.isNull(maxInactiveIntervalObj)) {
                continue;
            }
            long maxInactiveIntervalTime;
            if (maxInactiveIntervalObj instanceof Number) {
                maxInactiveIntervalTime = ((Number) maxInactiveIntervalObj).longValue();
            } else if (maxInactiveIntervalObj instanceof String) {
                maxInactiveIntervalTime = Long.parseLong((String) maxInactiveIntervalObj);
            } else {
                continue;
            }
            // 获取 lastAccessedTime
            Object lastAccessedTimeObj = map.get("lastAccessedTime");
            if (ObjUtil.isNull(lastAccessedTimeObj)) {
                continue;
            }
            long lastAccessedTime;
            if (lastAccessedTimeObj instanceof Number) {
                lastAccessedTime = ((Number) lastAccessedTimeObj).longValue();
            } else if (lastAccessedTimeObj instanceof String) {
                lastAccessedTime = Long.parseLong((String) lastAccessedTimeObj);
            } else {
                continue;
            }
            // 检查 session 是否过期
            Duration maxInactiveInterval = Duration.ofSeconds(maxInactiveIntervalTime);
            if (Instant.now().minus(maxInactiveInterval)
                    .compareTo(Instant.ofEpochMilli(lastAccessedTime)) >= 0) {
                continue;
            }
            // 获取安全上下文
            Object securityContextObj = map.get("sessionAttr:SPRING_SECURITY_CONTEXT");
            if (ObjUtil.isNull(securityContextObj)) {
                continue;
            }
            SecurityContextImpl securityContexts = (SecurityContextImpl) securityContextObj;
            Authentication authentication = securityContexts.getAuthentication();
            Object principal = authentication.getPrincipal();
            onlineUsers.add(principal);
        }
        return onlineUsers;
    }
}
