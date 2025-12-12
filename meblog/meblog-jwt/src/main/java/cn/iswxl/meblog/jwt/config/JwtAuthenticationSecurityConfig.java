package cn.iswxl.meblog.jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * JWT身份验证安全配置类
 * <p>
 * 该类用于配置Spring Security的身份验证机制，特别是JWT相关的安全配置。
 * 它扩展了SecurityConfigurerAdapter，允许自定义HTTP安全配置。
 * </p>
 */
@Configuration
public class JwtAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 配置HTTP安全策略
     * <p>
     * 此方法配置了DaoAuthenticationProvider作为身份验证提供者，
     * 设置了用户详细信息服务和密码编码器。
     * </p>
     *
     * @param httpSecurity HTTP安全配置对象，用于配置web安全
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        // 直接使用 DaoAuthenticationProvider, 它是 Spring Security 提供的默认的身份验证提供者之一
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // 设置 userDetailService，用于获取用户的详细信息
        provider.setUserDetailsService(userDetailsService);
        // 设置加密算法
        provider.setPasswordEncoder(passwordEncoder);
        httpSecurity.authenticationProvider(provider);
    }
}

