package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.tag.*;
import cn.iswxl.meblog.admin.service.AdminTagService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.jwt.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/tag")
@Tag(name = "Admin 标签模块")
public class AdminTagController {

    @Autowired
    private AdminTagService tagService;

    @PostMapping("/add")
    @Operation(description = "添加标签")
    @ApiOperationLog(description = "添加标签")
    @RequiresPermission(value = {"admin:tag:add"})
    public Response addTag(@RequestBody @Validated AddTagReqVO addTagReqVO) {
        return tagService.addTags(addTagReqVO);
    }

    @PostMapping("/list")
    @Operation(description = "标签分页数据获取")
    @ApiOperationLog(description = "标签分页数据获取")
    public PageResponse findTagPageList(@RequestBody @Validated FindTagPageListReqVO findTagPageListReqVO) {
        return tagService.findTagPageList(findTagPageListReqVO);
    }

    @PostMapping("/update")
    @Operation(description = "更新标签")
    @ApiOperationLog(description = "更新标签")
//    @RequiresPermission(value = {"admin:tag:update"})
    public Response updateTag(@RequestBody @Validated UpdateTagReqVO updateTagReqVO) {
        return tagService.updateTag(updateTagReqVO);
    }

    @PostMapping("/delete")
    @Operation(description = "删除标签")
    @ApiOperationLog(description = "删除标签")
    @RequiresPermission(value = {"admin:tag:delete"})
    public Response deleteTag(@RequestBody @Validated DeleteTagReqVO deleteTagReqVO) {
        return tagService.deleteTag(deleteTagReqVO);
    }

    @PostMapping("/search")
    @Operation(description = "标签模糊查询")
    @ApiOperationLog(description = "标签模糊查询")
    public Response searchTag(@RequestBody @Validated SearchTagReqVO searchTagReqVO) {
        return tagService.searchTag(searchTagReqVO);
    }

    @PostMapping("/select/list")
    @Operation(description = "查询标签 Select 列表数据")
    @ApiOperationLog(description = "查询标签 Select 列表数据")
    public Response findTagSelectList() {
        return tagService.findTagSelectList();
    }

}
