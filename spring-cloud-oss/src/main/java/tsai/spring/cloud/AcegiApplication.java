package tsai.spring.cloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
@SpringBootApplication
public class AcegiApplication {

    private static Logger logger = LoggerFactory.getLogger(AcegiApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AcegiApplication.class, args);
        Environment environment = context.getEnvironment();
        String port = environment.getProperty("server.port");
        String fetchRegistry = environment.getProperty("eureka.client.fetch-registry");
        String registryWithEureka = environment.getProperty("eureka.client.register-with-eureka");
        String defaultZone = environment.getProperty("eureka.client.service-url.defaultZone");
        String hostname = environment.getProperty("eureka.instance.hostname");
        String preferIpAddress = environment.getProperty("eureka.instance.prefer-ip-address");
        logger.info("===================认证服务运行环境信息===================");
        logger.info("服务端口：{}",port);
        logger.info("实例地址：{}",hostname);
        logger.info("是否从注册中心拉取服务：{}", fetchRegistry);
        logger.info("是否注册到注册中心：{}", registryWithEureka);
        logger.info("注册中心集群地址：{}", defaultZone);
        logger.info("是否优先使用服务器的IP地址：{}", preferIpAddress);
        logger.info("=======================================================");
    }
}
