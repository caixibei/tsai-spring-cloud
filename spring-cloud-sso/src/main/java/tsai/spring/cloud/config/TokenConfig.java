package tsai.spring.cloud.config;
import com.tsaiframework.boot.constant.WarningsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
/**
 * JWT 核心配置
 * @author tsai
 */
@Configuration
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class TokenConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public TokenStore tokenStore() {
        // JWT 模式
        // return new JwtTokenStore(tokenConverter());
        // 存储在数据库中
        // return new JdbcTokenStore(dataSource);
        // 存储在 Redis 中
        return new RedisTokenStore(redisConnectionFactory);
    }

    /**
     * 使用非对称加密算法对 Token 签名
     * @return {@link JwtAccessTokenConverter}
     */
    @Bean
    public JwtAccessTokenConverter tokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        return converter;
    }

    @Bean
    public KeyPair keyPair() {
        // 从证书文件 jwt.jks 中获取秘钥对
        // 密钥删除命令：keytool -delete -alias jwt -keystore jwt.jks
        // 密钥生成命令：keytool -genkey -alias jwt -keyalg RSA -keypass 123456 -keystore jwt.jks -storepass 123456
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (oAuth2AccessToken, oAuth2Authentication) -> {
            Map<String, Object> map = new HashMap<>(1);
            User user = (User) oAuth2Authentication.getPrincipal();
            map.put("username", user.getUsername());
            // ...其他信息可以自行添加
            ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(map);
            return oAuth2AccessToken;
        };
    }

}
