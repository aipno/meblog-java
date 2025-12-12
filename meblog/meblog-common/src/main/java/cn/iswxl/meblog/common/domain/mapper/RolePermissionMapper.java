package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.RolePermissionDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Set;

public interface RolePermissionMapper extends BaseMapper<RolePermissionDO> {

    /**
     * 根据角色ID查询角色权限关系
     *
     * @param roleId 角色ID
     * @return 角色权限关系列表
     */
    default List<RolePermissionDO> selectRolePermissions(Long roleId) {
        return selectList(new LambdaQueryWrapper<RolePermissionDO>().eq(RolePermissionDO::getRoleId, roleId));
    }

    default void deletePermissions(Integer roleId) {
        //TODO 角色颗粒权限管理
    }

    default void insertPermissions(Integer roleId, Set<Long> permissionIds) {
        //TODO 角色颗粒权限管理
    }

    /**
     * 修改角色权限关系
     *
     * @param roleId      角色ID
     * @param permissionIds 权限ID列表
     */
    default void updatePermissions(Long roleId, Set<Long> permissionIds) {
        RolePermissionDO rolePermissionDO = new RolePermissionDO();
        rolePermissionDO.setStatus(false);
        selectList(new LambdaQueryWrapper<RolePermissionDO>().eq(RolePermissionDO::getRoleId, roleId)).forEach(
                rolePermissionDO1 -> update(rolePermissionDO, new LambdaQueryWrapper<RolePermissionDO>().eq(RolePermissionDO::getRoleId, roleId))
        );
        permissionIds.forEach(permissionId -> {
            rolePermissionDO.setStatus(true);
            System.out.println(rolePermissionDO);
            update(rolePermissionDO, new LambdaQueryWrapper<RolePermissionDO>().eq(RolePermissionDO::getRoleId, roleId).eq(RolePermissionDO::getPermissionId, permissionId));
        });
    }

    /**
     * 根据角色ID和权限ID查询角色权限关系
     *
     * @param roleId      角色ID
     * @param permissionId 权限ID
     * @return 角色权限关系
     */
    default Boolean isStatus(Long roleId,Long permissionId) {
        return selectOne(new LambdaQueryWrapper<RolePermissionDO>().eq(RolePermissionDO::getRoleId, roleId).eq(RolePermissionDO::getPermissionId, permissionId)).isStatus();
    }
}
