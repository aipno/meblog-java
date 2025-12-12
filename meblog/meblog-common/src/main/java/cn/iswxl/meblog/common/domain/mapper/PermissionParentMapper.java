package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.PermissionParentDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface PermissionParentMapper extends BaseMapper<PermissionParentDO> {

    default PermissionParentDO selectByParentId(Integer parentId) {
        return selectOne(new LambdaQueryWrapper<PermissionParentDO>().eq(PermissionParentDO::getId, parentId));
    }


}
