package tsai.spring.cloud.service.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Service;

import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.RedisUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import tsai.spring.cloud.constant.RedisConstant;
import tsai.spring.cloud.service.SessionService;

/**
 * 会话管理
 *
 * @author tsai
 */
@Slf4j
@Service
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class SessionServiceImpl implements SessionService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisIndexedSessionRepository sessionRepository;

    @Override
    public List<Map<String, Object>> getOnlineUsers() {
        Set<Object> keys =
            sessionRepository.getSessionRedisOperations().keys(RedisConstant.SPRING_SESSION_PREFIX + "sessions:*");
        List<Map<String, Object>> list = new ArrayList<>();
        Object[] arr =
            {"lastAccessedTime", "maxInactiveInterval", "creationTime", "sessionAttr:SPRING_SECURITY_CONTEXT"};
        HashOperations<Object, Object, Object> operations = sessionRepository.getSessionRedisOperations().opsForHash();
        if (CollUtil.isNotEmpty(keys)) {
            keys.forEach(item -> {
                String itemString = String.valueOf(item);
                if (!itemString.contains("expires")) {
                    List<Object> values = operations.multiGet(item, Arrays.asList(arr));
                    Map<String, Object> re = new HashMap<>();
                    re.put("lastAccessedTime", values.get(0));
                    re.put("maxInactiveInterval", values.get(1));
                    re.put("creationTime", values.get(2));
                    re.put("sessionId", parseUserToken((String)item));
                    re.putAll(this.parseUserInfo(values.get(3)));
                    list.add(re);
                }
            });
        }
        return list;
    }

    protected String parseUserToken(String key) {
        if (StrUtil.isNotEmpty(key)) {
            String[] arr = key.split(":");
            if (arr.length == 4) {
                return arr[3];
            }
        }
        return null;
    }

    protected Map<String, Object> parseUserInfo(Object val) {
        Map<String, Object> userInfo = new HashMap<>();
        if (val instanceof SecurityContextImpl) {
            JSONObject json = JSONUtil.parseObj(val);
            if (json.containsKey("authentication")) {
                JSONObject authInfo = json.getJSONObject("authentication");
                if (authInfo != null) {
                    if (authInfo.containsKey("name")) {
                        userInfo.put("username", authInfo.getStr("name"));
                    }
                    if (authInfo.containsKey("details")) {
                        JSONObject details = authInfo.getJSONObject("details");
                        if (details != null && details.containsKey("remoteAddress")) {
                            userInfo.put("hostAddress", details.getStr("remoteAddress"));
                        }
                    }
                    if (authInfo.containsKey("principal")) {
                        JSONObject principal = authInfo.getJSONObject("principal");
                        if (principal.containsKey("sysUser")) {
                            JSONObject sysUser = principal.getJSONObject("sysUser");
                            userInfo.put("nickname", sysUser.getStr("nickname"));
                            userInfo.put("userCode", sysUser.getStr("userCode"));
                        }
                    }
                }
            }
        }
        return userInfo;
    }

}
