package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.article.*;
import cn.iswxl.meblog.admin.service.AdminArticleService;
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
@RequestMapping("/admin/article")
@Tag(name = "Admin 文章模块")
@RequiresPermission(PermissionConstants.Article.BASE)
public class AdminArticleController {

    private final AdminArticleService articleService;

    public AdminArticleController(AdminArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/publish")
    @Operation(description = "文章发布")
    @ApiOperationLog(description = "文章发布")
    @RequiresPermission(PermissionConstants.Article.PUBLISH)
    public Response publishArticle(@RequestBody @Validated PublishArticleReqVO publishArticleReqVO) {
        return articleService.publishArticle(publishArticleReqVO);
    }

    @PostMapping("/delete")
    @Operation(description = "文章删除")
    @ApiOperationLog(description = "文章删除")
    @RequiresPermission(PermissionConstants.Article.DELETE)
    public Response deleteArticle(@RequestBody @Validated DeleteArticleReqVO deleteArticleReqVO) {
        return articleService.deleteArticle(deleteArticleReqVO);
    }

    @PostMapping("/list")
    @Operation(description = "查询文章分页数据")
    @ApiOperationLog(description = "查询文章分页数据")
    @RequiresPermission(PermissionConstants.Article.LIST)
    public Response findArticlePageList(@RequestBody @Validated FindArticlePageListReqVO findArticlePageListReqVO) {
        return articleService.findArticlePageList(findArticlePageListReqVO);
    }

    @PostMapping("/detail")
    @Operation(description = "查询文章详情")
    @ApiOperationLog(description = "查询文章详情")
    @RequiresPermission(PermissionConstants.Article.DETAIL)
    public Response findArticleDetail(@RequestBody @Validated FindArticleDetailReqVO findArticlePageListReqVO) {
        return articleService.findArticleDetail(findArticlePageListReqVO);
    }

    @PostMapping("/update")
    @Operation(description = "更新文章")
    @ApiOperationLog(description = "更新文章")
    @RequiresPermission(PermissionConstants.Article.UPDATE)
    public Response updateArticle(@RequestBody @Validated UpdateArticleReqVO updateArticleReqVO) {
        return articleService.updateArticle(updateArticleReqVO);
    }

    @PostMapping("/isPublish/update")
    @Operation(description = "更新文章发布状态")
    @ApiOperationLog(description = "更新文章发布状态")
    @RequiresPermission(PermissionConstants.Article.IS_PUBLISH_UPDATE)
    public Response updateWikiIsPublish(@RequestBody @Validated UpdateArticleIsPublishReqVO updateArticleIsPublishReqVO) {
        return articleService.updateArticleIsPublish(updateArticleIsPublishReqVO);
    }

    @PostMapping("/isPermission/update")
    @Operation(description = "更新文章权限状态")
    @ApiOperationLog(description = "更新文章权限状态")
    @RequiresPermission(PermissionConstants.Article.IS_PERMISSION_UPDATE)
    public Response updateArticleIsPermission(@RequestBody @Validated UpdateArticlePermissionReqVO updateArticlePermissionReqVO) {
        return articleService.updateArticleIsPermission(updateArticlePermissionReqVO);
    }

}
