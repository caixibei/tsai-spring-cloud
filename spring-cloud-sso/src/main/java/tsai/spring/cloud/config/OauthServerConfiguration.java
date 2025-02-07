package tsai.spring.cloud.config;

import com.tsaiframework.boot.constant.WarningsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import tsai.spring.cloud.service.impl.UserDetailsServiceImpl;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Bean
    public ClientDetailsService clientDetails() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
        return jdbcClientDetailsService;
    }

    /**
     * 对于每个客户端应用，授权服务器会为其分配一个唯一的客户端ID和客户端密钥，并定义其授权范围和访问权限。
     * <p>用于配置客户端详细信息服务，这个服务用来定义哪些客户端可以访问授权服务器以及客户端的配置信息。
     *
     * @param clients the client details configurer
     * @throws Exception 异常
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 1.基于JDBC模式的密码认证
        clients.withClientDetails(clientDetails());
        // 2.以内存方式存储（不推荐）
        // 或 .autoApprove(true) 开启所有
        // clients.inMemory()
        //         .withClient("spring-cloud-system")
        //         .autoApprove("all")
        //         .secret("$2a$10$mcEwJ8qqhk2DYIle6VfhEOZHRdDbCSizAQbIwBR7tTuv9Q7Fca9Gi")
        //         .authorizedGrantTypes("authorization_code", "password", "refresh_token")
        //         .scopes("all")
        //         .redirectUris("http://localhost:7080/login")
        //         .accessTokenValiditySeconds(3600)
        //         // 刷新 token 的长 token 有效期 24 hours
        //         .refreshTokenValiditySeconds(86400);
    }

    /**
     * 用于配置授权服务器的安全性，如 /oauth/token、/oauth/authorize 等端点的安全性配置。
     *
     * @param security a fluent configurer for security features
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        // 允许客户端表单认证
        // security.allowFormAuthenticationForClients()
        //         // 需通过认证后才能访问 /oauth/token_key 获取 token 加密公钥
        //         .tokenKeyAccess("permitAll()")
        //         // 开放 /oauth/check_token
        //         .checkTokenAccess("permitAll()");
        // OSS 单点登录，允许客户端表单身份验证
        security.allowFormAuthenticationForClients()
                // 仅允许认证后的用户访问密钥端点
                .tokenKeyAccess("isAuthenticated()")
                // 允许所有人访问令牌验证端点
                .checkTokenAccess("permitAll()");
    }

    /**
     * 用于配置授权和令牌的端点，以及令牌服务、令牌存储、用户认证等相关配置。
     *
     * @param endpoints the endpoints configurer
     * @throws Exception 异常
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 开启密码模式授权，配置用于密码模式的 AuthenticationManager
        endpoints.authenticationManager(authenticationManager)
                // 自定义的 TokenService
                // .tokenServices(tokenServices(endpoints.getClientDetailsService()))
                // 在刷新令牌时使用此服务加载用户信息。
                .userDetailsService(userDetailsService)
                // token 解析器
                .accessTokenConverter(tokenConverter())
                // token 增强
                .tokenEnhancer(tokenEnhancer())
                // 以 redis 存储 token
                .tokenStore(tokenStore);
    }

    /**
     * 基于 TokenService 存储
     * <p>
     * 当使用有状态的登录的时候，会导致 Session 排他登录失效，具体原因暂时没搞懂！
     * @return {@link DefaultTokenServices}
     */
    @Bean
    public DefaultTokenServices tokenServices(ClientDetailsService clientDetailsService) {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        // 基于 redis 令牌生成
        tokenServices.setTokenStore(tokenStore);
        // 是否支持刷新令牌
        tokenServices.setSupportRefreshToken(true);
        // 是否重复使用刷新令牌（直到过期）
        tokenServices.setReuseRefreshToken(true);
        // 设置客户端信息
        tokenServices.setClientDetailsService(clientDetailsService);
        // 用来控制令牌存储增强策略
        tokenServices.setTokenEnhancer(tokenEnhancer());
        // 访问令牌的默认有效期（以秒为单位）。过期的令牌为零或负数。
        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.MINUTES.toSeconds(15));
        // 刷新令牌的有效性（以秒为单位）。如果小于或等于零，则令牌将不会过期。
        tokenServices.setRefreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(1));
        // 设置 token 增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Collections.singletonList(tokenConverter()));
        tokenServices.setTokenEnhancer(tokenEnhancerChain);
        return tokenServices;
    }

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
            // ...其他信息可以自行添加
            ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(map);
            return oAuth2AccessToken;
        };
    }
}
