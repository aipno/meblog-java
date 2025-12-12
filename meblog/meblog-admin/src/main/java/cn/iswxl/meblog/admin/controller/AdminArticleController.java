package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.article.*;
import cn.iswxl.meblog.admin.service.AdminArticleService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.jwt.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/article")
@Tag(name = "Admin 文章模块")
public class AdminArticleController {

    @Autowired
    private AdminArticleService articleService;

    @PostMapping("/publish")
    @Operation(description = "文章发布")
    @ApiOperationLog(description = "文章发布")
    @RequiresPermission(value = {"admin:article:publish"})
    public Response publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO) {
        return articleService.publishArticle(publishArticleReqVO);
    }

    @PostMapping("/delete")
    @Operation(description = "文章删除")
    @ApiOperationLog(description = "文章删除")
    @RequiresPermission(value = {"admin:article:delete"})
    public Response deleteArticle(@RequestBody @Validated DeleteArticleReqVO deleteArticleReqVO) {
        return articleService.deleteArticle(deleteArticleReqVO);
    }

    @PostMapping("/list")
    @Operation(description = "查询文章分页数据")
    @ApiOperationLog(description = "查询文章分页数据")
    @RequiresPermission(value = {"admin:article:list"})
    public Response findArticlePageList(@RequestBody @Validated FindArticlePageListReqVO findArticlePageListReqVO) {
        return articleService.findArticlePageList(findArticlePageListReqVO);
    }

    @PostMapping("/detail")
    @Operation(description = "查询文章详情")
    @ApiOperationLog(description = "查询文章详情")
    @RequiresPermission(value = {"admin:article:detail"})
    public Response findArticleDetail(@RequestBody @Validated FindArticleDetailReqVO findArticlePageListReqVO) {
        return articleService.findArticleDetail(findArticlePageListReqVO);
    }

    @PostMapping("/update")
    @Operation(description = "更新文章")
    @ApiOperationLog(description = "更新文章")
    @RequiresPermission(value = {"admin:article:update"})
    public Response updateArticle(@RequestBody @Validated UpdateArticleReqVO updateArticleReqVO) {
        return articleService.updateArticle(updateArticleReqVO);
    }

    @PostMapping("/isPublish/update")
    @Operation(description = "更新文章发布状态")
    @ApiOperationLog(description = "更新文章发布状态")
    @RequiresPermission(value = {"admin:article:status:update"})
    public Response updateWikiIsPublish(@RequestBody @Validated UpdateArticleIsPublishReqVO updateArticleIsPublishReqVO) {
        return articleService.updateArticleIsPublish(updateArticleIsPublishReqVO);
    }

    @PostMapping("/isPermission/update")
    @Operation(description = "更新文章权限状态")
    @ApiOperationLog(description = "更新文章权限状态")
    @RequiresPermission(value = {"admin:article:permission:update"})
    public Response updateArticleIsPermission(@RequestBody @Validated UpdateArticlePermissionReqVO updateArticlePermissionReqVO) {
        return articleService.updateArticleIsPermission(updateArticlePermissionReqVO);
    }

}
