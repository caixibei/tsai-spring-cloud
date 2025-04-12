package tsai.spring.cloud.service;
import java.util.List;
import java.util.Map;
public interface SessionService {

    List<Map<String, Object>> getOnlineUsers ();
}
