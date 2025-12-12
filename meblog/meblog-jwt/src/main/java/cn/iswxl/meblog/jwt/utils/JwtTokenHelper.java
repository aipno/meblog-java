package cn.iswxl.meblog.jwt.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenHelper implements InitializingBean {

    /**
     * 签发人
     */
    @Value("${jwt.issuer}")
    private String issuer;
    /**
     * 秘钥
     */
    private Key key;

    /**
     * JWT 解析
     */
    private JwtParser jwtParser;

    /**
     * Token 失效时间（分钟）
     */
    @Value("${jwt.tokenExpireTime}")
    private Long tokenExpireTime;

    /**
     * 解码配置文件中配置的 Base 64 编码 key 为秘钥
     */
    @Value("${jwt.secret}")
    public void setBase64Key(String base64Key) {
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Key));
    }

    /**
     * 初始化 JwtParser
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 考虑到不同服务器之间可能存在时钟偏移，setAllowedClockSkewSeconds 用于设置能够容忍的最大的时钟误差
        jwtParser = Jwts.parserBuilder().requireIssuer(issuer)
                .setSigningKey(key).setAllowedClockSkewSeconds(10)
                .build();
    }

    /**
     * 生成 Token
     */
    public String generateToken(String username) {
        LocalDateTime now = LocalDateTime.now();
        // Token 30分钟后失效
        LocalDateTime expireTime = now.plusMinutes(tokenExpireTime);
        
        // 生成JWT ID
        String jti = UUID.randomUUID().toString();

        return Jwts.builder().setSubject(username)
                .setId(jti)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expireTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 Token
     */
    public Jws<Claims> parseToken(String token) {
        try {
            return jwtParser.parseClaimsJws(token);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Token 不可用", e);
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("Token 失效", e);
        }
    }

    /**
     * 生成一个 Base64 的安全秘钥
     */
    private static String generateBase64Key() {
        // 生成安全秘钥
        Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // 将密钥进行 Base64 编码

        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static void main(String[] args) {
        String key = generateBase64Key();
        System.out.println("key: " + key);
    }

    /**
     * 校验 Token 是否可用
     */
    public void validateToken(String token) {
        jwtParser.parseClaimsJws(token);
    }

    /**
     * 解析 Token 获取用户名
     */
    public String getUsernameByToken(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("解析 Token 获取用户名失败", e);
        }
        return null;
    }
    
    /**
     * 解析 Token 获取 JWT ID
     */
    public String getJtiByToken(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return claims.getId();
        } catch (Exception e) {
            log.error("解析 Token 获取 JWT ID 失败", e);
        }
        return null;
    }
}
