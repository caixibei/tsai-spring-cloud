package tsai.spring.cloud.config;
import com.google.common.annotations.Beta;
import com.tsaiframework.boot.constant.WarningsConstants;
import com.tsaiframework.boot.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import tsai.spring.cloud.filter.JwtAuthenticationFilter;
import tsai.spring.cloud.handler.AccessDenyHandler;
import tsai.spring.cloud.handler.LoginFailureHandler;
import tsai.spring.cloud.handler.LoginSuccessHandler;
import tsai.spring.cloud.service.impl.UserDetailsServiceImpl;
import tsai.spring.cloud.strategy.SessionExpiredStrategy;
/**
 * Spring Security 核心配置
 *
 * @author tsai
 */
@Configuration
@EnableWebSecurity
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class SecurityConfiguration<S extends Session> extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AccessDenyHandler accessDeniedHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Beta
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private SessionExpiredStrategy sessionExpiredStrategy;

    @Beta
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private RedisIndexedSessionRepository sessionRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用 csrf
        http.csrf()
            //.disable()
            //.headers()
            //.frameOptions()
            //.disable()
            // 禁用 XSS 保护
            //.xssProtection()
            //.disable()
            // 禁用内容类型嗅探保护
            //.contentTypeOptions()
            .disable();
        // 认证拦截
        http.authorizeRequests()
            // 对静态资源、登录请求、获取token请求放行、获取验证码放行
            .antMatchers("/**/*.css", "/**/*.js", "/**/*.scss",
                    "/**/*.jpg","/**/*.png", "/**/*.gif", "/**/*.ico",
                    "/**/*.mp4", "/**/*.webm", "/**/*.json", "/**/*.ttf", "/**/*.woff",
                    "/**/*.woff2", "/login.html", "/error/403.html",
                    "/error", "/login", "/oauth/**", "/captcha/**")
            .permitAll()
            // 其他所有请求必须通过认证后才能访问
            .anyRequest().authenticated();
        // 异常处理器
        http.exceptionHandling()
            // 403：无权访问处理器
            .accessDeniedHandler(accessDeniedHandler);
        // 自定义的认证校验（无状态 JWT 使用）
        // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // 开启 session 会话管理（有状态的 session 登录可以使用）
        http.sessionManagement()
            // session 创建策略（有状态会话）
            .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            // 最多允许登录端数量
            .maximumSessions(1)
            // 超过最大数量是否阻止新的登录
            .maxSessionsPreventsLogin(false)
            // 会话注册表（spring session）
            .sessionRegistry(springSessionBackedSessionRegistry())
            // 会话过期策略
            .expiredSessionStrategy(sessionExpiredStrategy);
        // 开启表单登录（有状态的 session 登录可以使用）
        http.formLogin()
            // 自定义的登录页
            .loginPage("/login.html")
            // 必须和前端表单请求地址相同
            .loginProcessingUrl("/login")
            // 登录成功跳转页面
            .successForwardUrl("/home")
            // 登录失败处理器
            .failureHandler(loginFailureHandler);
            // 登录成功处理器
            // .successHandler(loginSuccessHandler)
            // 登录失败跳转页面
            // .failureForwardUrl("/failure");
    }

    @Bean
    public SpringSessionBackedSessionRegistry<? extends Session> springSessionBackedSessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
    }

    @Beta
    protected SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Beta
    protected ConcurrentSessionControlAuthenticationStrategy sessionAuthenticationStrategy() {
        ConcurrentSessionControlAuthenticationStrategy strategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        // 每个用户最多允许一个会话
        strategy.setMaximumSessions(1);
        // 不阻止新登录，而是使旧会话失效
        strategy.setExceptionIfMaximumExceeded(false);
        return strategy;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用自定义的 AuthenticationProvider 进行认证
        // auth.authenticationProvider(oAuthProvider);
        // 如果自定义的 provider 中已经配置了，这里无需再配置了
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
     *
     * @return the {@link PasswordEncoder} to use
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 二次封装的 Redis 工具
     *
     * @param redisTemplate 基础操作模板
     * @return 实例
     */
    @Bean
    public RedisUtil redisUtil(StringRedisTemplate redisTemplate) {
        return new RedisUtil(redisTemplate);
    }
}
