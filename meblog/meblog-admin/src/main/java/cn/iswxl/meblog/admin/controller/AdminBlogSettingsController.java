package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import cn.iswxl.meblog.admin.service.AdminBlogSettingsService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
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
@RequestMapping("/admin/blog/settings")
@Tag(name = "Admin 博客设置模块")
@RequiresPermission(PermissionConstants.BlogSettings.BASE)
public class AdminBlogSettingsController {

    private final AdminBlogSettingsService blogSettingsService;

    public AdminBlogSettingsController(AdminBlogSettingsService blogSettingsService) {
        this.blogSettingsService = blogSettingsService;
    }

    @PostMapping("/update")
    @Operation(description = "博客基础信息修改")
    @ApiOperationLog(description = "博客基础信息修改")
    @RequiresPermission(PermissionConstants.BlogSettings.UPDATE)
    public Response updateBlogSettings(@RequestBody @Validated UpdateBlogSettingsReqVO updateBlogSettingsReqVO) {
        return blogSettingsService.updateBlogSettings(updateBlogSettingsReqVO);
    }

    @PostMapping("/detail")
    @Operation(description = "获取博客设置详情")
    @ApiOperationLog(description = "获取博客设置详情")
    @RequiresPermission(PermissionConstants.BlogSettings.DETAIL)
    public Response findDetail() {
        return blogSettingsService.findDetail();
    }

}
