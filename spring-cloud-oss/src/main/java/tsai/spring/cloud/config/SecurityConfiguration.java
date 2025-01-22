package tsai.spring.cloud.config;
import com.tsaiframework.boot.constant.WarningsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tsai.spring.cloud.handler.AccessDenyHandler;
import tsai.spring.cloud.service.impl.UserDetailsServiceImpl;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@SuppressWarnings(WarningsConstants.SPRING_JAVA_AUTOWIRED_FIELDS_WARNING_INSPECTION)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AccessDenyHandler accessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .formLogin()
                .loginPage("/index.html")
                .loginProcessingUrl("/login")
                .successForwardUrl("/dashbord")
                .failureForwardUrl("/error")
                .and()
                .authorizeRequests()
                // 对静态资源、Oauth2 放行
                .antMatchers("/**/*.css", "/**/*.js", "/**/*.jpg",
                        "/**/*.png", "/**/*.gif", "/**/*.ico",
                        "/index.html","/login","/oauth/**","/error")
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

}
