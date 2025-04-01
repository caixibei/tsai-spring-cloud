package tsai.spring.cloud.config;

import com.tsaiframework.boot.constant.WarningsConstants;
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
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import tsai.spring.cloud.service.impl.UserDetailsServiceImpl;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;

/**
 * Oauth 2 授权服务配置
 *
 * @author tsai
 */
@Configuration
@EnableAuthorizationServer
@SuppressWarnings(WarningsConstants.UNUSED)
public class OauthServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final DataSource dataSource;

    private final RedisConnectionFactory redisConnectionFactory;

    public OauthServerConfiguration(AuthenticationManager authenticationManager,
                                    UserDetailsServiceImpl userDetailsService,
                                    RedisConnectionFactory redisConnectionFactory,
                                    PasswordEncoder passwordEncoder,
                                    DataSource dataSource) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.redisConnectionFactory = redisConnectionFactory;
        this.passwordEncoder = passwordEncoder;
        this.dataSource = dataSource;
    }

    @Bean
    public ClientDetailsService clientDetails() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
        return jdbcClientDetailsService;
    }

    /**
     * 授权码模式通过 {@code @Bean} 注解开启-授权码存储服务
     *
     * @return {@link AuthorizationCodeServices}
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
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
        // 使用基于JDBC模式的密码认证
        clients.withClientDetails(clientDetails());
    }

    /**
     * 用于配置授权服务器的安全性，如 /oauth/token、/oauth/authorize 等端点的安全性配置。
     *
     * @param security a fluent configurer for security features
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        // 允许客户端表单身份验证
        security.allowFormAuthenticationForClients()
                // 仅允许认证后的用户访问密钥端点（单点登录必须，如果是无状态的，请改为 permitAll() ）
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
        // 配置用于密码模式的认证管理器
        endpoints.authenticationManager(authenticationManager)
                // 授权码模式服务
                .authorizationCodeServices(authorizationCodeServices())
                // 在刷新令牌时使用此服务加载用户信息
                .userDetailsService(userDetailsService)
                // token 解析器
                .accessTokenConverter(tokenConverter())
                // token 扩展器
                .tokenEnhancer(tokenEnhancer())
                // 以 redis 存储 token
                .tokenStore(tokenStore());
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
        // 密钥删除命令：keytool -delete -alias jwt -keystore jwt.jks
        // 密钥生成命令：keytool -genkey -alias jwt -keyalg RSA -keypass 123456 -keystore jwt.jks -storepass 123456
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456" .toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456" .toCharArray());
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (oAuth2AccessToken, oAuth2Authentication) -> {
            Map<String, Object> map = new HashMap<>(1);
            User user = (User) oAuth2Authentication.getPrincipal();
            map.put("username", user.getUsername());
            map.put("authorities", user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            // ...其他信息可以自行添加
            ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(map);
            return oAuth2AccessToken;
        };
    }
}
