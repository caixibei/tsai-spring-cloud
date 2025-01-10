package tsai.spring.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
public class SecurityConfiguration {

    /**
     * 创建并配置一个 AuthenticationManager 实例，使用 DaoAuthenticationProvider 来处理用户身份验证。
     * DaoAuthenticationProvider 依赖于 UserDetailsService 和 PasswordEncoder 来验证用户信息。
     *
     * @return 返回一个 AuthenticationManager，负责身份验证的处理
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(Collections.singletonList(provider));
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
        return new BCryptPasswordEncoder();
    }

    /*@Deprecated
    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder builder, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Deprecated
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }*/
}
