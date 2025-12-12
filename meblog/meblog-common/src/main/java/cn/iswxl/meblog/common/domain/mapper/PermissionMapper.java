package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.PermissionDO;
import cn.iswxl.meblog.common.domain.dos.RolePermissionDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface PermissionMapper extends BaseMapper<PermissionDO> {

    default PermissionDO selectOnePermissions(Long permissionId) {
        return selectOne(new LambdaQueryWrapper<PermissionDO>().eq(PermissionDO::getId, permissionId));
    }

    default List<PermissionDO> selectByParentId(int parentId) {
        return selectList(new LambdaQueryWrapper<PermissionDO>().eq(PermissionDO::getParentId, parentId));
    }

}