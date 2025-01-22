package tsai.spring.cloud.config;

import com.tsaiframework.boot.constant.WarningsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import tsai.spring.cloud.pojo.User;
import tsai.spring.cloud.service.impl.UserDetailsServiceImpl;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * Oauth 2 授权服务配置
 *
 * @author tsai
 */
@Configuration
@EnableAuthorizationServer
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class OauthServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 1.基于JDBC模式的密码认证
        // JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        // clientDetailsService.setPasswordEncoder(passwordEncoder);
        // clients.withClientDetails(clientDetailsService);
        // 2.以内存方式存储（不推荐）
        clients.inMemory()
                .withClient("tsai-spring-cloud")
                .autoApprove(true)
                .secret("$2a$10$mcEwJ8qqhk2DYIle6VfhEOZHRdDbCSizAQbIwBR7tTuv9Q7Fca9Gi")
                .authorizedGrantTypes("authorization_code", "password", "refresh_token")
                .scopes("all")
                .redirectUris("http://localhost:7080/login")
                .accessTokenValiditySeconds(3600)
                // 刷新 token 的长 token 有效期 24 hours
                .refreshTokenValiditySeconds(86400);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        // OSS 单点登录
        security.allowFormAuthenticationForClients()
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("permitAll()");
        // 非单点登录
        // security.allowFormAuthenticationForClients()
        //         // 需通过认证后才能访问 /oauth/token_key 获取 token 加密公钥
        //         .tokenKeyAccess("permitAll()")
        //         // 开放 /oauth/check_token
        //         .checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        // List<TokenEnhancer> delegates = new ArrayList<>();
        // delegates.add(tokenEnhancer());
        // delegates.add(tokenConverter());
        // enhancerChain.setTokenEnhancers(delegates);
        // 开启密码模式授权
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .accessTokenConverter(tokenConverter())
                // 以 redis 存储 token
                .tokenStore(tokenStore);
    }

    @Bean
    public TokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    /**
     * 使用非对称加密算法对 Token 签名
     *
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
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "DdNwDFt2D5v5OVstBTr4h565ZRGVnSO7".toCharArray());
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (oAuth2AccessToken, oAuth2Authentication) -> {
            Map<String, Object> map = new HashMap<>(1);
            User user = (User) oAuth2Authentication.getPrincipal();
            map.put("username", user.getUsername());
            // 其他信息可以自行添加
            ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(map);
            return oAuth2AccessToken;
        };
    }
}
