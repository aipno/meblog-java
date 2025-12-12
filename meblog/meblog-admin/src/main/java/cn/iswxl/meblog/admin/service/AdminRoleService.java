package cn.iswxl.meblog.admin.service;

import cn.iswxl.meblog.admin.model.vo.role.ChangeRolePermissionReqVO;
import cn.iswxl.meblog.admin.model.vo.user.ChangeUserRoleReqVO;
import cn.iswxl.meblog.admin.model.vo.role.FindRoleUserInfoListReqVO;
import cn.iswxl.meblog.admin.model.vo.role.FindRoleUserInfoListRspVO;
import cn.iswxl.meblog.common.utils.Response;

import java.util.List;

public interface AdminRoleService {

    /**
     * 查询用户组所有用户信息
     *
     * @param reqVO 查询参数
     * @return 用户组所有用户信息
     */
    List<FindRoleUserInfoListRspVO> findRoleUserInfoList(FindRoleUserInfoListReqVO reqVO);

    /**
     * 修改用户角色
     *
     * @param reqVO 修改参数
     * @return 修改结果
     */
    Response changeUserRole(ChangeUserRoleReqVO reqVO);

    /**
     * 修改角色权限
     *
     * @param reqVO 修改参数
     * @return 修改结果
     */
    Response changeRolePermission(ChangeRolePermissionReqVO reqVO);
}
