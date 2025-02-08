package tsai.spring.cloud.config;
import com.tsaiframework.boot.constant.WarningsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import tsai.spring.cloud.service.impl.UserDetailsServiceImpl;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
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
    private TokenStore tokenStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JwtAccessTokenConverter tokenConverter;

    @Autowired
    private TokenEnhancer tokenEnhancer;

    @Bean
    public ClientDetailsService clientDetails() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
        return jdbcClientDetailsService;
    }

    /**
     * 授权码模式-授权码存储服务
     * @return {@link AuthorizationCodeServices}
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices () {
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
        // 配置 Token 增强
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(tokenEnhancer);
        delegates.add(tokenConverter);
        enhancerChain.setTokenEnhancers(delegates);
        // 配置用于密码模式的认证管理器
        endpoints.authenticationManager(authenticationManager)
                // 授权码服务
                .authorizationCodeServices(authorizationCodeServices())
                // 在刷新令牌时使用此服务加载用户信息。
                .userDetailsService(userDetailsService)
                // token 解析器
                .tokenEnhancer(enhancerChain)
                // 以 redis 存储 token
                .tokenStore(tokenStore);
    }
}
