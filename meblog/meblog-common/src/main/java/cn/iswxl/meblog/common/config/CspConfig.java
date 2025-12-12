package cn.iswxl.meblog.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CSP安全策略配置
 */
@Configuration
public class CspConfig {

    @Bean
    public FilterRegistrationBean<CspFilter> cspFilter() {
        FilterRegistrationBean<CspFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CspFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("cspFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    /**
     * CSP过滤器，用于设置内容安全策略响应头
     */
    public static class CspFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            // 设置CSP头，限制脚本来源，只允许同源脚本执行
            response.setHeader("Content-Security-Policy",
                    "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                            "style-src 'self' 'unsafe-inline'; " +
                            "img-src 'self' data: https:; " +
                            "font-src 'self' data:; " +
                            "connect-src 'self'; " +
                            "frame-ancestors 'none'; " +
                            "form-action 'self';");

            // 设置X-Content-Type-Options头，防止MIME类型嗅探
            response.setHeader("X-Content-Type-Options", "nosniff");

            // 设置X-Frame-Options头，防止点击劫持
            response.setHeader("X-Frame-Options", "DENY");

            // 设置X-XSS-Protection头，启用浏览器内置XSS保护
            response.setHeader("X-XSS-Protection", "1; mode=block");

            filterChain.doFilter(request, response);
        }
    }
}
