package cn.iswxl.meblog.admin.service;

import cn.iswxl.meblog.common.utils.Response;

public interface AdminPermissionService {


    /**
     * 获取角色权限列表
     *
     * @param roleId
     * @return
     */
    Response findPermissionWithParentList(Long roleId);
}
