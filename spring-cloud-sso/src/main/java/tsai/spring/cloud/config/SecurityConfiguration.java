package tsai.spring.cloud.config;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tsai.spring.cloud.handler.AccessDenyHandler;
import tsai.spring.cloud.service.impl.UserDetailsServiceImpl;
import tsai.spring.cloud.strategy.SessionExpiredStrategy;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AccessDenyHandler accessDeniedHandler;

    @Autowired
    private SessionExpiredStrategy sessionExpiredStrategy;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .formLogin()
                .loginPage("/index_v1.html")
                .loginProcessingUrl("/login")
                .successForwardUrl("/dashboard")
                .failureForwardUrl("/error")
                .and()
                .authorizeRequests()
                // 对静态资源、Oauth2 放行
                .antMatchers(
                        // 静态资源放行
                        "/**/*.css", "/**/*.js", "/**/*.jpg",
                        "/**/*.png", "/**/*.gif", "/**/*.ico",
                        "/**/*.json", "/**/*.ttf", "/**/*.woff",
                        "/**/*.woff2","/index_v1.html","/error",
                        // 登录请求、获取token请求放行、获取验证码放行
                        "/login","/oauth/**", "/sso/lineCaptcha")
                .permitAll()
                // 其他所有请求必须通过认证后才能访问
                .anyRequest().authenticated()
                // 异常处理器
                .and().exceptionHandling()
                    // 403：无权访问处理器
                    .accessDeniedHandler(accessDeniedHandler)
                // 多人登录限制，强制下线
                .and().sessionManagement()
                    // 最多允许登录端数量
                    .maximumSessions(1)
                    // 多端登录session失效的策略
                    .expiredSessionStrategy(sessionExpiredStrategy)
                    // 超过最大数量是否阻止新的登录
                    .maxSessionsPreventsLogin(false);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置一个全局统一共享的PasswordEncoder（密码编码器）
     * @return the {@link PasswordEncoder} to use
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 二次封装的 Redis 工具
     * @param redisTemplate 基础操作模板
     * @return 实例
     */
    @Bean
    public RedisUtil redisUtil(StringRedisTemplate redisTemplate) {
        return new RedisUtil(redisTemplate);
    }
}
