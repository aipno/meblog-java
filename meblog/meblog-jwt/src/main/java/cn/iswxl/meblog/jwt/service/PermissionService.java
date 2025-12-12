package cn.iswxl.meblog.jwt.service;

import java.util.Set;

public interface PermissionService {

    /**
     * 缓存用户权限信息
     * 将指定用户的权限信息加载到缓存中，以便后续快速访问
     *
     * @param username 用户名，用于标识需要缓存权限信息的用户
     */
    void cacheUserPermissions(String username);

    /**
     * 获取用户权限集合
     * 根据用户ID查询该用户所拥有的所有权限标识
     *
     * @param userId 用户唯一标识符
     * @return 用户权限字符串集合，如果用户不存在或无权限则返回空集合
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 获取用户的所有角色名
     */
    String getUserRole(Long userId);

    /**
     * 验证用户是否拥有指定权限
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * 验证用户是否拥有指定角色
     */
    boolean hasRole(Long userId, String role);
}

