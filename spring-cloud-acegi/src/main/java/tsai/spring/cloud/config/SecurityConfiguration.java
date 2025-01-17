package tsai.spring.cloud.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tsai.spring.cloud.handler.AccessDenyHandler;
import tsai.spring.cloud.handler.LoginFailureHandler;
import tsai.spring.cloud.handler.LoginSuccessHandler;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private LoginSuccessHandler successHandler;

    @Autowired
    private LoginFailureHandler failureHandler;

    @Autowired
    private AccessDenyHandler accessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                // 开启登录功能，自定义登录页面
                .formLogin()
                .loginPage("/index.html")
                // 登录成功处理器
                .successHandler(successHandler)
                // 登录失败处理器
                .failureHandler(failureHandler)
                // 开启权限控制
                .and()
                .authorizeRequests()
                // 对静态资源放行
                .antMatchers("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.gif", "/**/*.ico", "/index.html")
                .permitAll()
                // 其他所有请求必须通过认证后才能访问
                .anyRequest().authenticated()
                // 异常处理器
                .and().exceptionHandling()
                    // 403：无权访问处理器
                    .accessDeniedHandler(accessDeniedHandler);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * 配置一个全局统一共享的PasswordEncoder（密码编码器），例如:
     * <ul>
     * <li>bcrypt - {@link BCryptPasswordEncoder} (也用于编码)</li>
     * <li>ldap - {@link org.springframework.security.crypto.password.LdapShaPasswordEncoder}</li>
     * <li>MD4 - {@link org.springframework.security.crypto.password.Md4PasswordEncoder}</li>
     * <li>MD5 - {@code new MessageDigestPasswordEncoder("MD5")}</li>
     * <li>noop - {@link org.springframework.security.crypto.password.NoOpPasswordEncoder}</li>
     * <li>SHA-1 - {@code new MessageDigestPasswordEncoder("SHA-1")}</li>
     * <li>SHA-256 - {@code new MessageDigestPasswordEncoder("SHA-256")}</li>
     * <li>sha256 - {@link org.springframework.security.crypto.password.StandardPasswordEncoder}</li>
     * </ul>
     *
     * @return the {@link PasswordEncoder} to use
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
