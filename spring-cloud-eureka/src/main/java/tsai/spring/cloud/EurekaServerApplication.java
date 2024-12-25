package tsai.spring.cloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {
    private static Logger logger = LoggerFactory.getLogger(EurekaServerApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EurekaServerApplication.class, args);
        Environment environment = context.getEnvironment();
        String port = environment.getProperty("server.port");
        logger.info("");
    }
}
