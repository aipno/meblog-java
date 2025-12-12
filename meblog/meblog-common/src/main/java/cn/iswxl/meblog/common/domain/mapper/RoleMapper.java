package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.RoleDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface RoleMapper extends BaseMapper<RoleDO> {

    /**
     * 根据角色ID查询角色名称
     *
     * @param roleId 角色ID
     * @return 角色名称
     */
    default String selectRoleNamesByRoleId(Long roleId) {
        RoleDO roleDO = selectOne(new LambdaQueryWrapper<RoleDO>().eq(RoleDO::getId, roleId));
        return roleDO != null ? roleDO.getRoleName() : null;
    }

    default List<RoleDO> selectAllRoles() {
        return selectList(new LambdaQueryWrapper<RoleDO>());
    }

}
