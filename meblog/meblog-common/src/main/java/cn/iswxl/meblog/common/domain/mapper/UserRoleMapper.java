package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.UserRoleDO;
import cn.iswxl.meblog.common.utils.Response;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface UserRoleMapper extends BaseMapper<UserRoleDO> {

    /**
     * 根据用户ID查询用户角色id
     * 
     * @param userId 用户ID
     * @return 角色ID字符串
     */
    default Long selectUserRole(Long userId) {
        UserRoleDO userRoleDO = selectOne(new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId));
        return userRoleDO != null ? userRoleDO.getRoleId() : null;
    }

    /**
     * 插入用户角色关系
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否成功
     */
    default void insertUserRole(Long userId, Long roleId) {
        UserRoleDO userRoleDO = new UserRoleDO();
        if (userId != null || roleId != null) {
            userRoleDO.setUserId(userId);
            userRoleDO.setRoleId(roleId);
            insert(userRoleDO);
        }
    }

    /**
     * 根据角色ID查询用户ID
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    default List<Long> selectUserIdsByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getRoleId, roleId))
                .stream().map(UserRoleDO::getUserId).toList();
    }

    /**
     * 修改用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    default void changeUserRole(Long userId, Long roleId) {
        if (userId == null || roleId == null) {
            return;
        }
        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setUserId(userId);
        userRoleDO.setRoleId(roleId);
        update(userRoleDO, new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId));
    }

}
