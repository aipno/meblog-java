package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.category.*;
import cn.iswxl.meblog.admin.service.AdminCategoryService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.utils.PageResponse;
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
@Tag(name = "Admin 分类模块")
@RequiresPermission(PermissionConstants.Category.BASE)
public class AdminCategoryController {

    private final AdminCategoryService categoryService;

    public AdminCategoryController(AdminCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/category/add")
    @Operation(description = "添加分类")
    @ApiOperationLog(description = "添加分类")
    @RequiresPermission(PermissionConstants.Category.CREATE)
    public Response addCategory(@RequestBody @Validated AddCategoryReqVO addCategoryReqVO) {
        return categoryService.addCategory(addCategoryReqVO);
    }

    @PostMapping("/category/list")
    @Operation(description = "分类分页数据获取")
    @ApiOperationLog(description = "分类分页数据获取")
    @RequiresPermission(PermissionConstants.Category.LIST)
    public PageResponse<FindCategoryPageListRspVO> findCategoryPageList(@RequestBody @Validated FindCategoryPageListReqVO findCategoryPageListReqVO) {
        return categoryService.findCategoryPageList(findCategoryPageListReqVO);
    }

    @PostMapping("/category/delete")
    @Operation(description = "删除分类")
    @ApiOperationLog(description = "删除分类")
    @RequiresPermission(PermissionConstants.Category.DELETE)
    public Response deleteCategory(@RequestBody @Validated DeleteCategoryReqVO deleteCategoryReqVO) {
        return categoryService.deleteCategory(deleteCategoryReqVO);
    }

    @PostMapping("/category/update")
    @Operation(description = "更新分类")
    @ApiOperationLog(description = "更新分类")
    @RequiresPermission(PermissionConstants.Category.UPDATE)
    public Response updateCategory(@RequestBody @Validated UpdateCategoryReqVO updateCategoryReqVO) {
        return categoryService.updateCategory(updateCategoryReqVO);
    }

    @PostMapping("/category/select/list")
    @Operation(description = "分类 Select 下拉列表数据获取")
    @ApiOperationLog(description = "分类 Select 下拉列表数据获取")
    @RequiresPermission(PermissionConstants.Category.LIST)
    public Response findCategorySelectList() {
        return categoryService.findCategorySelectList();
    }


}
