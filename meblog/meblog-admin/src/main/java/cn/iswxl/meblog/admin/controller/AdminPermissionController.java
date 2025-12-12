package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.permission.FindPermissionListReqVO;
import cn.iswxl.meblog.admin.service.AdminPermissionService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
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
@Tag(name = "Admin 权限模块")
public class AdminPermissionController {

    @Autowired
    private AdminPermissionService adminPermissionService;

    @PostMapping("/permission/list")
    @Operation(description = "获取角色所有权限列表")
    @ApiOperationLog(description = "获取角色所有权限列表")
    public Response getPermissionList(@RequestBody @Validated FindPermissionListReqVO findPermissionListReqVO) {
        return adminPermissionService.findPermissionWithParentList(findPermissionListReqVO.getRoleId());
    }
}
