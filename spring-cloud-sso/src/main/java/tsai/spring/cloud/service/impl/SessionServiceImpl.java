package tsai.spring.cloud.service.impl;

import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import tsai.spring.cloud.service.SessionService;

import java.io.IOException;
@Slf4j
@Service
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class SessionServiceImpl implements SessionService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Override
    public Page<String> getOnlineUsers() {
        return null;
    }
}
