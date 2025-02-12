package tsai.spring.cloud.config;
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
import tsai.spring.cloud.handler.AccessDenyHandler;
import tsai.spring.cloud.handler.LoginFailureHandler;
import tsai.spring.cloud.service.impl.UserDetailsServiceImpl;
import tsai.spring.cloud.strategy.SessionExpiredStrategy;
@Configuration
@EnableWebSecurity
@SuppressWarnings({WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION, WarningsConstants.UNUSED})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AccessDenyHandler accessDeniedHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private SessionExpiredStrategy sessionExpiredStrategy;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 使用无状态的登录方式
        // http.csrf().disable()
        //         .formLogin()
        //         // 自定义的登录页
        //         .loginPage("/login.html")
        //         // 必须和前端表单请求地址相同
        //         .loginProcessingUrl("/login")
        //         // 登录成功跳转页面
        //         .successForwardUrl("/dashboard")
        //         // 登录失败跳转页面
        //         .failureForwardUrl("/error")
        //         // 登录失败处理器
        //         .failureHandler(loginFailureHandler)
        //         .and()
        //         .authorizeRequests()
        //         // 对静态资源、登录请求、获取token请求放行、获取验证码放行
        //         .antMatchers("/**/*.css", "/**/*.js", "/**/*.jpg",
        //                 "/**/*.png", "/**/*.gif", "/**/*.ico",
        //                 "/**/*.json", "/**/*.ttf", "/**/*.woff",
        //                 "/**/*.woff2", "/login.html", "/403.html",
        //                 "/error", "/login", "/oauth/**", "/sso/lineCaptcha")
        //         .permitAll()
        //         // 其他所有请求必须通过认证后才能访问
        //         .anyRequest().authenticated()
        //         // 异常处理器
        //         .and().exceptionHandling()
        //         // 403：无权访问处理器
        //         .accessDeniedHandler(accessDeniedHandler)
        //         .and().sessionManagement().disable();
        // 使用有状态的会话管理，资源服务器要关闭 @EnableResourceServer 的注解
        http.csrf().disable()
            .formLogin()
            // 自定义的登录页
            .loginPage("/login.html")
            // 必须和前端表单请求地址相同
            .loginProcessingUrl("/login")
            // 登录成功跳转页面
            .successForwardUrl("/index")
            // 登录失败跳转页面
            .failureForwardUrl("/error")
            // 登录失败处理器
            .failureHandler(loginFailureHandler)
            .and()
                .authorizeRequests()
                // 对静态资源、登录请求、获取token请求放行、获取验证码放行
                .antMatchers("/**/*.css", "/**/*.js", "/**/*.jpg",
                        "/**/*.png", "/**/*.gif", "/**/*.ico",
                        "/**/*.mp4", "/**/*.webm",
                        "/**/*.json", "/**/*.ttf", "/**/*.woff",
                        "/**/*.woff2", "/login.html","/error/error.html",
                        "/error", "/login", "/oauth/**", "/sso/lineCaptcha")
                .permitAll()
                // 其他所有请求必须通过认证后才能访问
                .anyRequest().authenticated()
            // fixed 校验 Bearer Token 是否正确，暂时没有使用
            // .and().addFilterAfter(new BearerTokenAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
            // 异常处理器
            .and().exceptionHandling()
                // 403：无权访问处理器
                .accessDeniedHandler(accessDeniedHandler)
            // 开启 session 会话管理
            .and().sessionManagement()
                 // session 创建策略（无状态会话）
                 .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                 // 应用并发会话策略机制（暂不开启）
                 //.sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                 // 最多允许登录端数量
                 .maximumSessions(1)
                 // 多端登录session失效的策略
                 .expiredSessionStrategy(sessionExpiredStrategy)
                 // 超过最大数量是否阻止新的登录
                 .maxSessionsPreventsLogin(false);
    }

    public SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

    public ConcurrentSessionControlAuthenticationStrategy sessionAuthenticationStrategy() {
        ConcurrentSessionControlAuthenticationStrategy strategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        // 限制每个用户只能有一个会话
        strategy.setMaximumSessions(1);
        // 超过最大会话数时抛出异常
        strategy.setExceptionIfMaximumExceeded(true);
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
