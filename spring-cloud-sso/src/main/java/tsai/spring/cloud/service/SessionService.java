package tsai.spring.cloud.service;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface SessionService {

    Page<String> getOnlineUsers ();
}
