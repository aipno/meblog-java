package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.service.AdminDashboardService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@Tag(name = "Admin 仪表盘")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    @PostMapping("/statistics")
    @Operation(description = "获取后台仪表盘基础统计信息")
    @ApiOperationLog(description = "获取后台仪表盘基础统计信息")
    public Response findDashboardStatistics() {
        return dashboardService.findDashboardStatistics();
    }

    @PostMapping("/publishArticle/statistics")
    @Operation(description = "获取后台仪表盘文章发布热点统计信息")
    @ApiOperationLog(description = "获取后台仪表盘文章发布热点统计信息")
    public Response findDashboardPublishArticleStatistics() {
        return dashboardService.findDashboardPublishArticleStatistics();
    }

    @PostMapping("/pv/statistics")
    @Operation(description = "获取后台仪表盘最近一周 PV 访问量信息")
    @ApiOperationLog(description = "获取后台仪表盘最近一周 PV 访问量信息")
    public Response findDashboardPVStatistics() {
        return dashboardService.findDashboardPVStatistics();
    }
}
