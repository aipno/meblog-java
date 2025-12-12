package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.role.ChangeRolePermissionReqVO;
import cn.iswxl.meblog.admin.model.vo.role.FindRoleUserInfoListReqVO;
import cn.iswxl.meblog.admin.service.AdminRoleService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.domain.mapper.RoleMapper;
import cn.iswxl.meblog.common.utils.Response;
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
public class AdminRoleController {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private AdminRoleService adminRoleService;

    @PostMapping("/role/list")
    @Operation(description = "获取角色列表")
    @ApiOperationLog(description = "获取角色列表")
    // TODO 权限控制
//    @RequiresPermission(value = {"admin:role:list"})
    public Response getRoleList() {
        return Response.success(roleMapper.selectAllRoles());
    }

    @PostMapping("/role/user")
    @Operation(description = "获取角色所有用户列表")
    @ApiOperationLog(description = "获取角色所有用户列表")
    public Response getRoleUserList(@RequestBody @Validated FindRoleUserInfoListReqVO reqVO) {
        return Response.success(adminRoleService.findRoleUserInfoList(reqVO));
    }

    @PostMapping("/role/permission/change")
    @Operation(description = "修改角色权限")
    @ApiOperationLog(description = "修改角色权限")
    public Response changeRolePermission(@RequestBody @Validated ChangeRolePermissionReqVO reqVO) {
        return adminRoleService.changeRolePermission(reqVO);
    }
}
