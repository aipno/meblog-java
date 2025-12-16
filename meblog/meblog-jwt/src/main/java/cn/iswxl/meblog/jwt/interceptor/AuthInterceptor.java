package cn.iswxl.meblog.jwt.interceptor;

import cn.iswxl.meblog.common.context.UserContext;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.jwt.utils.JwtTokenHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Value("${jwt.tokenHeaderKey:Authorization}")
    private String tokenHeaderKey;

    @Value("${jwt.tokenPrefix:Bearer }")
    private String tokenPrefix;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法（例如静态资源），直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 1. 获取 Token
        String header = request.getHeader(tokenHeaderKey);
        if (StringUtils.isBlank(header) || !header.startsWith(tokenPrefix)) {
            // 这里可以根据是否是“强制登录”接口决定是否抛异常
            // 简单做法：只要配置了拦截路径，就必须有 Token，否则由全局异常处理器捕获返回 401
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
        }

        String token = header.substring(tokenPrefix.length());

        // 2. 校验 Token (复用你现有的 JwtTokenHelper)
        try {
            // validateToken 内部抛出异常说明 Token 无效
            jwtTokenHelper.validateToken(token);

            // 3. 解析用户信息
            String username = jwtTokenHelper.getUsernameByToken(token);
            // 假设你可以在 Token 中放入 userId，或者通过 username 去 Redis/DB 查 userId
            // 为了性能，建议在生成 Token 时把 userId 放入 Claims，这里直接解析
             String userName = jwtTokenHelper.getUsernameByToken(token);

            // 4. 设置上下文
            UserContext.setUsername(userName);

        } catch (Exception e) {
            log.error("Token 认证失败", e);
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束，清理 ThreadLocal，防止内存泄漏
        UserContext.remove();
    }
}