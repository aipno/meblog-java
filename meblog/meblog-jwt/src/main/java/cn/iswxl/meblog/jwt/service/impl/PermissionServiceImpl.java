package cn.iswxl.meblog.jwt.service.impl;

import cn.iswxl.meblog.common.domain.dos.*;
import cn.iswxl.meblog.common.domain.mapper.*;
import cn.iswxl.meblog.common.utils.RedisUtils;
import cn.iswxl.meblog.jwt.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 查询用户权限并缓存到Redis
     * @param username 用户名
     */
    public void cacheUserPermissions(String username) {
        try {
            // 查询用户信息
            UserDO userDO = userMapper.findByUsername(username);
            if (userDO != null) {
                // 构建用户权限缓存键
                String permissionKey = "user:permissions:" + userDO.getId();

                // 从Redis中获取权限信息
                Object cachedPermissions = redisUtils.get(permissionKey);

                if (cachedPermissions == null) {
                    Set<String> permissions = getUserPermissions(userDO.getId());
                    // 将权限信息缓存到Redis，设置6小时过期时间
                    if (permissions != null && !permissions.isEmpty()) {
                        redisUtils.set(permissionKey, permissions, 21600L); // 6小时
                    } else {
                        // 如果用户没有权限，缓存空集合
                        redisUtils.set(permissionKey, Collections.emptySet(), 18000L); // 5小时
                    }
                }
            }
        } catch (Exception e) {
            log.warn("缓存用户权限时发生异常: {}", e.getMessage());
        }
    }

    /**
     * 获取用户权限集合
     *
     * @param userId 用户ID
     * @return 用户权限码集合
     */
    public Set<String> getUserPermissions(Long userId) {
        // 查询用户的角色和权限
        // 1. 查询用户角色
        // 通过用户ID查询用户所属角色关系
        UserRoleDO userRole = userRoleMapper.selectOne(new LambdaQueryWrapper<UserRoleDO>()
                .eq(UserRoleDO::getUserId, userId));

        // 如果用户没有角色，返回空集合
        if (userRole.getRoleId() == null) {
            return Collections.emptySet();
        }

        // 2. 查询角色权限关系
        // 通过用户所属角色查询用户所拥有的权限
        Long roleId = userRole.getRoleId();

        // 使用 rolePermissionMapper 调用 selectList 方法，传入构造好的 LambdaQueryWrapper 查询条件；
        // 条件为 RolePermissionDO 表中 roleId 字段等于指定角色ID；
        // 查询结果为一个 List<RolePermissionDO>，即该角色对应的权限关系数据。
        List<RolePermissionDO> rolePermissions = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermissionDO>()
                        .in(RolePermissionDO::getRoleId, roleId));

        // 如果角色没有权限，返回空集合
        if (rolePermissions.isEmpty()) {
            return Collections.emptySet();
        }

        // 3. 查询权限码
        // 通过权限ID查询权限码
        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermissionDO::getPermissionId)
                .collect(Collectors.toList());

        List<PermissionDO> permissionDOS = permissionMapper.selectBatchIds(permissionIds);

        // 4. 提取权限码
        return permissionDOS.stream()
                .map(PermissionDO::getPermCode)
                .collect(Collectors.toSet());
    }

    @Override
    public String getUserRole(Long userId) {
        Long roleId = userRoleMapper.selectUserRole(userId);
        return roleMapper.selectRoleNamesByRoleId(roleId);
    }

    /**
     * 判断用户是否具有指定权限
     * @param userId 用户ID
     * @param permission 权限码
     * @return 是否具有权限
     */
    @Override
    public boolean hasPermission(Long userId, String permission) {
        Set<String> userPermissions = getUserPermissions(userId);
        return userPermissions.contains(permission);
    }

    /**
     * 判断用户是否具有指定角色
     * @param userId 用户ID
     * @param role 角色
     * @return 是否具有角色
     */
    @Override
    public boolean hasRole(Long userId, String role) {
        Long userRoleId = userRoleMapper.selectUserRole(userId);
        String userRole = roleMapper.selectRoleNamesByRoleId(userRoleId);
        return userRole.equals(role);
    }
}
