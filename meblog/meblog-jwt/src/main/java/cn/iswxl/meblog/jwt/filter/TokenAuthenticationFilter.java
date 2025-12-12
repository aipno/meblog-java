package cn.iswxl.meblog.jwt.filter;

import cn.iswxl.meblog.common.domain.dos.UserDO;
import cn.iswxl.meblog.common.domain.mapper.UserMapper;
import cn.iswxl.meblog.common.utils.JsonUtil;
import cn.iswxl.meblog.common.utils.RedisUtils;
import cn.iswxl.meblog.jwt.utils.JwtTokenHelper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Token 认证过滤器（Token解析）
 *
 */

@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    // Redis缓存用户信息的过期时间（秒）
    private static final long USER_CACHE_EXPIRE_TIME = 30 * 60; // 30分钟
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;
    @Value("${jwt.tokenHeaderKey}")
    private String tokenHeaderKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 获取请求头中的token
        String header = request.getHeader(tokenHeaderKey);

        // 判断 value 值是否以 Bearer 开头
        if (StringUtils.startsWith(header, tokenPrefix)) {
            // 截取 Token 令牌
            String token = StringUtils.substring(header, 7);
            log.info("Token: {}", token);

            // 判空 Token
            if (StringUtils.isNotBlank(token)) {
                try {
                    // 校验 Token 是否可用, 若解析异常，针对不同异常做出不同的响应参数
                    jwtTokenHelper.validateToken(token);
                } catch (SignatureException | MalformedJwtException | UnsupportedJwtException |
                         IllegalArgumentException e) {
                    // 抛出异常，统一让 AuthenticationEntryPoint 处理响应参数
                    authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 不可用"));
                    return;
                } catch (ExpiredJwtException e) {
                    authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Token 已失效"));
                    return;
                }

                // 从 Token 中解析出用户名
                String username = jwtTokenHelper.getUsernameByToken(token);

                if (StringUtils.isNotBlank(username)
                        && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {

                    // 根据用户名获取用户详情信息（包含权限信息）
                    UserDetails userDetails;
                    try {
                        userDetails = userDetailsService.loadUserByUsername(username);
                    } catch (Exception e) {
                        log.error("加载用户详情失败，username: {}", username, e);
                        authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("用户不存在"));
                        return;
                    }

                    // 先从Redis缓存中获取用户信息用于状态检查
                    String userCacheKey = "user:info:" + username;
                    Object userObj = redisUtils.get(userCacheKey);
                    UserDO userDO = null;
                    
                    // 处理从Redis中获取的对象，可能是JSONObject类型
                    if (userObj != null) {
                        if (userObj instanceof UserDO) {
                            userDO = (UserDO) userObj;
                        } else {
                            // 如果是从Redis获取的是字符串形式的JSON，需要转换为UserDO对象
                            String jsonString = userObj.toString();
                            userDO = JsonUtil.parseObject(jsonString, UserDO.class);
                        }
                    }

                    // 如果缓存中没有，则从数据库查询并存入缓存
                    if (userDO == null) {
                        userDO = userMapper.findByUsername(username);
                        if (userDO != null) {
                            redisUtils.set(userCacheKey, userDO, USER_CACHE_EXPIRE_TIME);
                        }
                    }

                    // 检查用户是否存在
                    if (userDO == null) {
                        authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("用户不存在"));
                        return;
                    }

                    // 检查用户状态
                    if (!userDO.getStatus()) {
                        // 当用户被禁用（status为false）时，通过authenticationEntryPoint返回"用户被禁用"的错误信息，并终止请求处理。
                        authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("用户被禁用"));
                        return;
                    }

                    // 将用户信息存入 authentication，方便后续校验
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将 authentication 存入 ThreadLocal，方便后续获取用户信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // 继续执行写一个过滤器
        filterChain.doFilter(request, response);
    }
}

