package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.role.ChangeRolePermissionReqVO;
import cn.iswxl.meblog.admin.model.vo.role.FindRoleUserInfoListReqVO;
import cn.iswxl.meblog.admin.service.AdminRoleService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.domain.mapper.RoleMapper;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.jwt.annotation.RequiresPermission;
import cn.iswxl.meblog.jwt.constant.PermissionConstants;
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
@Tag(name = "Admin 角色模块")
@RequiresPermission(PermissionConstants.Role.BASE)
public class AdminRoleController {

    private final RoleMapper roleMapper;
    private final AdminRoleService adminRoleService;

    public AdminRoleController(RoleMapper roleMapper, AdminRoleService adminRoleService) {
        this.roleMapper = roleMapper;
        this.adminRoleService = adminRoleService;
    }

    @PostMapping("/role/list")
    @Operation(description = "获取角色列表")
    @ApiOperationLog(description = "获取角色列表")
    @RequiresPermission(PermissionConstants.Role.LIST)
    public Response getRoleList() {
        return Response.success(roleMapper.selectAllRoles());
    }

    @PostMapping("/role/user")
    @Operation(description = "获取角色所有用户列表")
    @ApiOperationLog(description = "获取角色所有用户列表")
    @RequiresPermission(PermissionConstants.Role.LIST_ALL)
    public Response getRoleUserList(@RequestBody @Validated FindRoleUserInfoListReqVO reqVO) {
        return Response.success(adminRoleService.findRoleUserInfoList(reqVO));
    }

    @PostMapping("/role/permission/change")
    @Operation(description = "修改角色权限")
    @ApiOperationLog(description = "修改角色权限")
    @RequiresPermission(PermissionConstants.Role.UPDETE_PERMISSION)
    public Response changeRolePermission(@RequestBody @Validated ChangeRolePermissionReqVO reqVO) {
        return adminRoleService.changeRolePermission(reqVO);
    }
}
