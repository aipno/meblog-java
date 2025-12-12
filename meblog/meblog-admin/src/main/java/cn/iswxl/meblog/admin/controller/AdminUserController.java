package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.user.ChangeUserRoleReqVO;
import cn.iswxl.meblog.admin.model.vo.user.ChangeUserStatusReqVO;
import cn.iswxl.meblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import cn.iswxl.meblog.admin.service.AdminRoleService;
import cn.iswxl.meblog.admin.service.AdminUserService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.jwt.annotation.RequiresPermission;
import cn.iswxl.meblog.jwt.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 用户模块")
public class AdminUserController {

    @Autowired
    private AdminUserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminRoleService adminRoleService;

    @PostMapping("/password/update")
    @Operation(description = "修改用户密码")
    @ApiOperationLog(description = "修改用户密码")
    public Response updatePassword(@RequestBody @Validated UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        return userService.updatePassword(updateAdminUserPasswordReqVO);
    }

    @PostMapping("/password/reset")
    @Operation(description = "重置用户密码")
    @ApiOperationLog(description = "重置用户密码")
    @RequiresPermission(value = {"admin:user:password:reset"})
    public Response resetPassword(@RequestBody @Validated UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) {
        return userService.resetPassword(updateAdminUserPasswordReqVO);
    }

    @PostMapping("/user/info")
    @Operation(description = "获取用户信息")
    @ApiOperationLog(description = "获取用户信息")
    public Response findUserInfo() {
        return userService.findUserInfo();
    }

    @PostMapping("/user/logout")
    @Operation(description = "用户退出登录")
    @ApiOperationLog(description = "用户退出登录")
    public Response logout() {
        authService.logout();
        return Response.success();
    }

    @PostMapping("/user/list")
    @Operation(description = "获取所有用户")
    @ApiOperationLog(description = "获取所有用户")
    public Response findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/user/status")
    @Operation(description = "修改用户状态")
    @ApiOperationLog(description = "修改用户状态")
    // TODO 权限
//    @RequiresPermission(value = {"admin:user:status:update"})
    public Response updateUserStatus(@RequestBody @Validated ChangeUserStatusReqVO changeUserStatusReqVO) {
        return userService.changeUserStatus(changeUserStatusReqVO.getUserId(),changeUserStatusReqVO.getStatus());
    }

    @PostMapping("/user/role/change")
    @Operation(description = "修改用户的角色")
    @ApiOperationLog(description = "修改用户的角色")
    public Response changeRoleUser(@RequestBody @Validated ChangeUserRoleReqVO reqVO) {
        return adminRoleService.changeUserRole(reqVO);
    }
}
