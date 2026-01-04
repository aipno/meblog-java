package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.service.AdminFileService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.model.BasePageQuery;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.jwt.annotation.RequiresPermission;
import cn.iswxl.meblog.jwt.constant.PermissionConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 文件模块")
@RequiresPermission(PermissionConstants.File.BASE)
public class AdminFileController {

    private final AdminFileService fileService;

    public AdminFileController(AdminFileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/file/upload")
    @Operation(description = "文件上传")
    @ApiOperationLog(description = "文件上传")
    @RequiresPermission(PermissionConstants.File.UPLOAD)
    public Response uploadFile(@RequestParam MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @PostMapping("/image/list")
    @Operation(description = "获取图片列表")
    @ApiOperationLog(description = "获取图片列表")
    @RequiresPermission(PermissionConstants.File.IMAGE_LIST)
    public Response findImageList() {
        return Response.success(fileService.findSelectImagePageList());
    }

    @PostMapping("/file/object/list")
    @Operation(description = "获取文件列表")
    @ApiOperationLog(description = "获取文件列表")
    @RequiresPermission(PermissionConstants.File.LIST)
    public Response findObjectFileList() {
        return Response.success(fileService.findObjectList());
    }

    @PostMapping("/file/page")
    @Operation(description = "分页获取文件列表")
    @ApiOperationLog(description = "分页获取文件列表")
    @RequiresPermission(PermissionConstants.File.PAGE)
    public PageResponse findFilePageList(@RequestBody BasePageQuery basePageQuery) {
        return fileService.findFilePageList(basePageQuery.getCurrent(), basePageQuery.getSize());
    }

    @PostMapping("/file/delete")
    @Operation(description = "文件删除")
    @ApiOperationLog(description = "文件删除")
    @RequiresPermission(PermissionConstants.File.DELETE)
    public Response deleteFile(@RequestParam String id) {
        //TODO 文件删除
        return null;
    }

}