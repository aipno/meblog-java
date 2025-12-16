package cn.iswxl.meblog.jwt.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import cn.iswxl.meblog.common.context.UserContext;
import cn.iswxl.meblog.common.domain.dos.UserDO;
import cn.iswxl.meblog.common.domain.mapper.UserMapper;
import cn.iswxl.meblog.common.domain.mapper.UserRoleMapper;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.common.service.EmailService;
import cn.iswxl.meblog.common.utils.RedisUtils;
import cn.iswxl.meblog.jwt.model.certification.LoginReqVO;
import cn.iswxl.meblog.jwt.model.certification.LoginRspVO;
import cn.iswxl.meblog.jwt.service.AuthService;
import cn.iswxl.meblog.jwt.service.PermissionService;
import cn.iswxl.meblog.jwt.utils.JwtTokenHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 授权登录接口实现类
 * (已移除 Spring Security 依赖)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private PermissionService permissionService;

    @Value("${spring.mail.expiration}")
    private Long expiration;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public LoginRspVO login(LoginReqVO loginReqVO) {
        String username = loginReqVO.getUsername();
        String password = loginReqVO.getPassword();

        // 1. 基础参数校验
        if (StringUtils.isBlank(username)) {
            throw new BizException(ResponseCodeEnum.USERNAME_OR_PWD_REQUIRED);
        }
        if (StringUtils.isBlank(password)) {
            throw new BizException(ResponseCodeEnum.USERNAME_OR_PWD_REQUIRED);
        }

        // 2. 查询用户
        UserDO user = userMapper.findByUsername(username);

        // 3. 校验用户是否存在
        if (user == null) {
            // 为了安全，通常提示“用户名或密码错误”，防止暴力枚举用户名
            throw new BizException(ResponseCodeEnum.USERNAME_OR_PWD_ERROR);
        }

        // 4. 校验密码 (替代 AuthenticationManager)
        // 使用 BCrypt.checkpw(明文密码, 数据库中的加密哈希)
        if (!BCrypt.checkpw(password, user.getPassword())) {
            log.warn("用户认证失败：密码错误，username: {}", username);
            throw new BizException(ResponseCodeEnum.USERNAME_OR_PWD_ERROR);
        }

        // 5. 校验用户状态
        if (Boolean.FALSE.equals(user.getStatus())) {
            throw new BizException(ResponseCodeEnum.USER_STATUS_IS_FALSE);
        }

        log.info("用户认证成功：{}", username);

        // 登录接口本身不需要设置 Context，因为它是无状态的，生成 Token 返回即可。
        // 下一次请求时，Interceptor 会根据 Token 设置 UserContext。

        // 6. 查询并缓存权限 (保持原有逻辑)
        permissionService.cacheUserPermissions(username);

        // 7. 生成 Token
        String token = jwtTokenHelper.generateToken(username);

        // 8. 处理单点登录 (Redis 缓存 Token)
        redisUtils.set("user:token:" + username, token, 35 * 60);

        return new LoginRspVO(token);
    }

    // 注册方法逻辑更新建议 (即使目前被注释，也展示如何替换加密部分)
    /*
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(RegisterReqVO registerReqVO) {
        // ... 省略校验逻辑 ...

        // 创建新用户
        UserDO userDO = UserDO.builder()
                .username(username)
                // 替换 passwordEncoder.encode(password) 为 BCrypt.hashpw
                .password(BCrypt.hashpw(password))
                .email(email)
                .status(true)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // ... 省略插入逻辑 ...
        return result > 0;
    }
    */

    @Override
    public void logout() {
        // 1. 从 UserContext 获取当前用户 ID (替代 SecurityContextHolder)
        Long userId = UserContext.getUserId();
        if (userId == null) {
            // 用户未登录，直接返回
            return;
        }

        // 2. 获取用户名 (因为 Redis Key 依赖用户名)
        // 建议：如果 UserContext 中能存 username 最好，否则这里需要查一次库
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }
        String username = user.getUsername();

        // 3. 获取当前 Redis 中存储的 Token (用于加入黑名单)
        String tokenKey = "user:token:" + username;
        String token = (String) redisUtils.get(tokenKey);

        // 4. 清理 Redis 缓存
        // 清除权限缓存
        redisUtils.del("permissions:" + username);
        // 清除单点登录 Token
        redisUtils.del(tokenKey);

        // 5. Token 加入黑名单
        if (token != null) {
            String jti = jwtTokenHelper.getJtiByToken(token);
            if (jti != null) {
                // 黑名单过期时间与 Token 有效期保持一致
                redisUtils.set("blacklist:token:" + jti, "1", 35 * 60);
            }
        }

        // 6. 清理当前线程上下文 (可选，Interceptor 的 afterCompletion 也会清理)
        UserContext.remove();

        log.info("用户退出登录成功：{}", username);
    }
}