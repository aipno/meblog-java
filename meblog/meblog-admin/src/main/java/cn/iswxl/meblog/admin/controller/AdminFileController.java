package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.service.AdminFileService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.model.BasePageQuery;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.jwt.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin")
@Tag(name = "Admin 文件模块")
public class AdminFileController {

    @Autowired
    private AdminFileService fileService;

    @PostMapping("/file/upload")
    @Operation(description = "文件上传")
    @ApiOperationLog(description = "文件上传")
    @RequiresPermission(value = {"admin:file:upload"})
    public Response uploadFile(@RequestParam MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @PostMapping("/image/list")
    @Operation(description = "获取图片列表")
    @ApiOperationLog(description = "获取图片列表")
    @RequiresPermission(value = {"admin:image:list"})
    public Response findImageList() {
        return Response.success(fileService.findSelectImagePageList());
    }

    @PostMapping("/file/object/list")
    @Operation(description = "获取对象文件列表")
    @ApiOperationLog(description = "获取对象文件列表")
//    @RequiresPermission(value = {"admin:file:object:list"})
    public Response findObjectFileList() {
        return Response.success(fileService.findObjectList());
    }
    
    @PostMapping("/file/page")
    @Operation(description = "分页获取文件列表")
    @ApiOperationLog(description = "分页获取文件列表")
    @RequiresPermission(value = {"admin:file:list"})
    public PageResponse findFilePageList(@RequestBody BasePageQuery basePageQuery) {
        return fileService.findFilePageList(basePageQuery.getCurrent(), basePageQuery.getSize());
    }
}