package cn.iswxl.meblog.admin.service;

import cn.iswxl.meblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import cn.iswxl.meblog.common.utils.Response;

import java.util.List;

public interface AdminUserService {
    /**
     * 修改密码
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);

    /**
     * 获取当前登录用户信息
     * @return
     */
    Response findUserInfo();

    /**
     * 获取所有用户信息
     * @return
     */
    Response findAllUsers();

    /**
     * 修改用户状态
     * @param userId
     * @param status
     * @return
     */
    Response changeUserStatus(Long userId, Integer status);

    /**
     * 重置密码
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    Response resetPassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO);
}

