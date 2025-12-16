package cn.iswxl.meblog.jwt.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.iswxl.meblog.common.domain.dos.UserDO;
import cn.iswxl.meblog.common.domain.mapper.UserMapper;
import cn.iswxl.meblog.common.domain.mapper.UserRoleMapper;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.common.model.vo.email.SendEmailRspVO;
import cn.iswxl.meblog.common.service.EmailService;
import cn.iswxl.meblog.common.utils.RedisUtils;
import cn.iswxl.meblog.jwt.exception.UsernameOrPasswordNullException;
import cn.iswxl.meblog.jwt.model.certification.LoginReqVO;
import cn.iswxl.meblog.jwt.model.certification.LoginRspVO;
import cn.iswxl.meblog.jwt.model.certification.RegisterReqVO;
import cn.iswxl.meblog.jwt.service.AuthService;
import cn.iswxl.meblog.jwt.service.PermissionService;
import cn.iswxl.meblog.jwt.utils.JwtTokenHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * 授权登录接口实现类
 *
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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private PermissionService permissionService;

    @Value("${spring.mail.expiration}")
    private Long expiration;
    @Autowired
    private UserRoleMapper userRoleMapper;

//    @Override
//    public void sendMailCode(String email) {
//        // 检查邮箱是否为空
//        if (email == null || email.trim().isEmpty()) {
//            throw new RuntimeException("邮箱地址不能为空");
//        }
//
//        // 检查60秒冷却时间
//        String cooldownKey = "cooldown:" + email;
//        if (redisUtils.get(cooldownKey) != null) {
//            throw new RuntimeException("邮件发送过于频繁，请稍后再试");
//        }
//
//        // 查看注册邮箱是否存在
//        if (userMapper.existsByEmail(email)) {
//            throw new RuntimeException("注册邮箱已存在");
//        }
//
//        // 获取发送邮箱验证码的HTML模板
//        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("/templates/", TemplateConfig.ResourceMode.CLASSPATH));
//        Template template = engine.getTemplate("email-code.ftl");
//
//        // 从redis缓存中尝试获取验证码
//        Object code = redisUtils.get(email);
//        if (code == null) {
//            // 如果在缓存中未获取到验证码，则产生6位随机数，放入缓存中
//            code = RandomUtil.randomNumbers(6);
//            if (!redisUtils.set(email, code, expiration)) {
//                throw new RuntimeException("后台缓存服务异常");
//            }
//        }
//
//        // 设置60秒冷却时间
//        redisUtils.set(cooldownKey, "1", 60);
//
//        // 发送验证码
//        String content = template.render(Dict.create().set("code", code));
//        emailService.send(new SendEmailRspVO(Collections.singletonList(email),
//                "邮箱验证码", content));
//
//    }

    @Override
    public LoginRspVO login(LoginReqVO loginReqVO) {
        String username = loginReqVO.getUsername();
        String password = loginReqVO.getPassword();

        // 判断用户名、密码是否为空
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameOrPasswordNullException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new UsernameOrPasswordNullException("密码不能为空");
        }

        // 查询用户是否被禁用
        UserDO user = userMapper.findByUsername(username);
        if (user.getStatus().equals(false)) {
            throw new BizException(ResponseCodeEnum.USER_STATUS_IS_FALSE);
        }

        // 使用AuthenticationManager进行认证
        log.info("开始认证用户：{}", username);
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            log.error("用户认证失败：{}", username);
            throw new BizException(ResponseCodeEnum.USERNAME_OR_PWD_ERROR);
        }
        log.info("用户认证成功：{}", username);


        // 将认证信息存储到安全上下文中
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 查询用户权限并缓存到Redis
        permissionService.cacheUserPermissions(username);

        // 生成JWT Token
        String token = jwtTokenHelper.generateToken(username);

        // 将用户与Token的映射关系存储到Redis中，实现单点登录
        // 过期时间比Token的过期时间多5分钟，确保Token完全过期
        // 转换为秒：35分钟 = 35 * 60秒
        redisUtils.set("user:token:" + username, token, 35 * 60);

        return new LoginRspVO(token);
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean register(RegisterReqVO registerReqVO) {
//        String username = registerReqVO.getUsername();
//        String password = registerReqVO.getPassword();
//        String confirmPassword = registerReqVO.getConfirmPassword();
//        String email = registerReqVO.getEmail();
//        String code = registerReqVO.getCode();
//
//        // 验证参数
//        if (username == null || username.trim().isEmpty() ||
//                password == null || password.trim().isEmpty() ||
//                confirmPassword == null || confirmPassword.trim().isEmpty() ||
//                !confirmPassword.equals(password) ||
//                email == null || email.trim().isEmpty() ||
//                code == null || code.trim().isEmpty()) {
//            throw new RuntimeException("所有字段都必须填写");
//        }
//
//        // 验证邮箱验证码
//        Object cachedCode = redisUtils.get(email);
//        if (cachedCode == null || !cachedCode.toString().equals(code)) {
//            throw new RuntimeException("验证码错误或已过期");
//        }
//
//        // 检查用户名是否已存在
//        UserDO existingUser = userMapper.findByUsername(username);
//        if (existingUser != null) {
//            throw new RuntimeException("用户名已存在");
//        }
//
//        // 检查邮箱是否已存在
//        if (userMapper.existsByEmail(email)) {
//            throw new RuntimeException("邮箱已被注册");
//        }
//
//        // 创建新用户
//        UserDO userDO = UserDO.builder()
//                .username(username)
//                .password(passwordEncoder.encode(password))
//                .email(email)
//                .status(true)
//                .createTime(LocalDateTime.now())
//                .updateTime(LocalDateTime.now())
//                .isDeleted(false)
//                .build();
//
//        // 保存用户信息
//        int result = userMapper.insert(userDO);
//
//        // 设置角色权限
//        userRoleMapper.insertUserRole(userDO.getId(), 3L);
//
//        // 删除已使用的验证码
//        redisUtils.del(email);
//
//        return result > 0;
//    }

    @Override
    public void logout() {
        // 通过用户名清除上下文
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            String username = authentication.getPrincipal().toString();
            String token = null;

            // 获取当前Token
            Object details = authentication.getDetails();
            // 由于无法直接从Authentication中获取Token，我们从Redis中获取当前用户的Token
            token = (String) redisUtils.get("user:token:" + username);

            // 清除用户权限缓存
            redisUtils.del("permissions:" + username);

            // 从Redis中删除用户Token映射
            redisUtils.del("user:token:" + username);

            // 将Token加入黑名单
            if (token != null) {
                String jti = jwtTokenHelper.getJtiByToken(token);
                if (jti != null) {
                    // 将JWT ID加入黑名单，过期时间为35分钟（与user:token过期时间一致）
                    redisUtils.set("blacklist:token:" + jti, "1", 35 * 60);
                }
            }
        }
        // 清除安全上下文
        SecurityContextHolder.clearContext();
    }
}
