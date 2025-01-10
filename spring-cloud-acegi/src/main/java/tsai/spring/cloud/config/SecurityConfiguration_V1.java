package tsai.spring.cloud.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
@EnableWebSecurity
public class SecurityConfiguration_V1 extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

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
