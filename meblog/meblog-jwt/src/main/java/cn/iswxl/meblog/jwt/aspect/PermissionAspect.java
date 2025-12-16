package cn.iswxl.meblog.jwt.aspect;

import cn.iswxl.meblog.common.context.UserContext;
import cn.iswxl.meblog.common.domain.mapper.UserMapper;
import cn.iswxl.meblog.common.enums.LogicalEnum;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.jwt.annotation.RequiresPermission;
import cn.iswxl.meblog.jwt.annotation.RequiresRoles;
import cn.iswxl.meblog.jwt.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class PermissionAspect {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 权限注解的切点
     */
    @Pointcut("@annotation(cn.iswxl.meblog.jwt.annotation.RequiresPermission)")
    public void permissionPointcut() {
    }

    /**
     * 角色注解的切点
     */
    @Pointcut("@annotation(cn.iswxl.meblog.jwt.annotation.RequiresRoles)")
    public void rolePointcut() {
    }

    /**
     * 权限验证通知
     */
    @Around("permissionPointcut()")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RequiresPermission requiresPermission = method.getAnnotation(RequiresPermission.class);
        if (requiresPermission != null) {
            // 获取当前用户
            Long userId = UserContext.getUserId();

            if (userId == null) {
                throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
            }

            // 权限验证
            String[] permissions = requiresPermission.value();
            LogicalEnum logical = requiresPermission.logical();

            boolean hasPermission = checkPermissions(userId, permissions, logical);
            if (!hasPermission) {
                throw new BizException(ResponseCodeEnum.FORBIDDEN);
            }
        }

        return joinPoint.proceed();
    }

    /**
     * 角色验证通知
     */
    @Around("rolePointcut()")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RequiresRoles requiresRoles = method.getAnnotation(RequiresRoles.class);
        if (requiresRoles != null) {
            Long userId = UserContext.getUserId();
            if (userId == null) {
                throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
            }

            String[] roles = requiresRoles.value();
            LogicalEnum logical = requiresRoles.logical();

            boolean hasRole = checkRoles(userId, roles, logical);
            if (!hasRole) {
                throw new BizException(ResponseCodeEnum.FORBIDDEN);
            }
        }

        return joinPoint.proceed();
    }

    /**
     * 验证权限
     */
    private boolean checkPermissions(Long userId, String[] permissions, LogicalEnum logical) {
        if (permissions.length == 0) {
            return true;
        }

        Set<String> userPermissions = permissionService.getUserPermissions(userId);

        if (logical == LogicalEnum.AND) {
            // 必须拥有所有权限
            for (String permission : permissions) {
                if (!userPermissions.contains(permission)) {
                    return false;
                }
            }
            return true;
        } else {
            // 拥有任意一个权限即可
            for (String permission : permissions) {
                if (userPermissions.contains(permission)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 验证角色
     */
    private boolean checkRoles(Long userId, String[] roles, LogicalEnum logical) {
        String userRoles = permissionService.getUserRole(userId);

        if (logical == LogicalEnum.AND) {
            for (String role : roles) {
                if (!userRoles.contains(role)) {
                    return false;
                }
            }
            return true;
        } else {
            for (String role : roles) {
                if (userRoles.contains(role)) {
                    return true;
                }
            }
            return false;
        }
    }
}