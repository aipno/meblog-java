package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.wiki.*;
import cn.iswxl.meblog.admin.service.AdminWikiService;
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
@RequestMapping("/admin/wiki")
@Tag(name = "Admin 知识库模块")
@RequiresPermission(PermissionConstants.Wiki.BASE)
public class AdminWikiController {

    private final AdminWikiService wikiService;

    public AdminWikiController(AdminWikiService wikiService) {
        this.wikiService = wikiService;
    }

    @PostMapping("/add")
    @Operation(description = "新增知识库")
    @ApiOperationLog(description = "新增知识库")
    @RequiresPermission(PermissionConstants.Wiki.CREATE)
    public Response<Object> addWiKi(@RequestBody @Validated AddWikiReqVO addWiKiReqVO) {
        return wikiService.addWiKi(addWiKiReqVO);
    }

    @PostMapping("/delete")
    @Operation(description = "删除知识库")
    @ApiOperationLog(description = "删除知识库")
    @RequiresPermission(PermissionConstants.Wiki.DELETE)
    public Response deleteWiKi(@RequestBody @Validated DeleteWikiReqVO deleteWiKiReqVO) {
        return wikiService.deleteWiKi(deleteWiKiReqVO);
    }

    @PostMapping("/list")
    @Operation(description = "查询知识库分页数据")
    @ApiOperationLog(description = "查询知识库分页数据")
    @RequiresPermission(PermissionConstants.Wiki.LIST)
    public Response findWikiPageList(@RequestBody @Validated FindWikiPageListReqVO findWikiPageListReqVO) {
        return wikiService.findWikiPageList(findWikiPageListReqVO);
    }

    @PostMapping("/isTop/update")
    @Operation(description = "更新知识库置顶状态")
    @ApiOperationLog(description = "更新知识库置顶状态")
    @RequiresPermission(PermissionConstants.Wiki.IS_TOP_UPDATE)
    public Response updateWikiIsTop(@RequestBody @Validated UpdateWikiIsTopReqVO updateWikiIsTopReqVO) {
        return wikiService.updateWikiIsTop(updateWikiIsTopReqVO);
    }

    @PostMapping("/isPublish/update")
    @Operation(description = "更新知识库发布状态")
    @ApiOperationLog(description = "更新知识库发布状态")
    @RequiresPermission(PermissionConstants.Wiki.IS_PUBLISH_UPDATE)
    public Response updateWikiIsPublish(@RequestBody @Validated UpdateWikiIsPublishReqVO updateWikiIsPublishReqVO) {
        return wikiService.updateWikiIsPublish(updateWikiIsPublishReqVO);
    }

    @PostMapping("/update")
    @Operation(description = "更新知识库")
    @ApiOperationLog(description = "更新知识库")
    @RequiresPermission(PermissionConstants.Wiki.UPDATE)
    public Response updateWiki(@RequestBody @Validated UpdateWikiReqVO updateWikiReqVO) {
        return wikiService.updateWiki(updateWikiReqVO);
    }

    @PostMapping("/catalog/list")
    @Operation(description = "查询知识库目录数据")
    @ApiOperationLog(description = "查询知识库目录数据")
    @RequiresPermission(PermissionConstants.Wiki.CATALOG_LIST)
    public Response findWikiCatalogList(@RequestBody @Validated FindWikiCatalogListReqVO findWikiCatalogListReqVO) {
        return wikiService.findWikiCatalogList(findWikiCatalogListReqVO);
    }

    @PostMapping("/catalog/update")
    @Operation(description = "更新知识库目录")
    @ApiOperationLog(description = "更新知识库目录")
    @RequiresPermission(PermissionConstants.Wiki.CATALOG_UPDATE)
    public Response updateWikiCatalogs(@RequestBody @Validated UpdateWikiCatalogReqVO updateWikiCatalogsReqVO) {
        return wikiService.updateWikiCatalogs(updateWikiCatalogsReqVO);
    }
}
