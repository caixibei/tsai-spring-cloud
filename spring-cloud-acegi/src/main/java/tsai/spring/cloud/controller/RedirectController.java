package tsai.spring.cloud.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Controller
public class RedirectController {
    @PostMapping("/dashboard")
    public void dashboard (HttpServletResponse response) throws IOException {
        response.sendRedirect("http://127.0.0.1:9000/dashboard.html");
    }

    @PostMapping("/error")
    public void error (HttpServletResponse response) throws IOException {
        response.sendRedirect("http://127.0.0.1:9000/error.html");
    }
}
