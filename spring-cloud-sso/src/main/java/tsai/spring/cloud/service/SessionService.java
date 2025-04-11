package tsai.spring.cloud.service;
import org.springframework.security.core.userdetails.UserDetails;
public interface SessionService {

    UserDetails getOnlineUsers ();
}
