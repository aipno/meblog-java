package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.admin.model.vo.dashboard.FindDashboardStatisticsInfoRspVO;
import cn.iswxl.meblog.admin.service.AdminDashboardService;
import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.jwt.annotation.RequiresPermission;
import cn.iswxl.meblog.jwt.constant.PermissionConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@Tag(name = "Admin 仪表盘")
@RequiresPermission(PermissionConstants.Dashboard.BASE)
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PostMapping("/statistics")
    @Operation(description = "获取后台仪表盘基础统计信息")
    @ApiOperationLog(description = "获取后台仪表盘基础统计信息")
    @RequiresPermission(PermissionConstants.Dashboard.STATISTICS)
    public Response<FindDashboardStatisticsInfoRspVO> findDashboardStatistics() {
        return dashboardService.findDashboardStatistics();
    }

    @PostMapping("/publishArticle/statistics")
    @Operation(description = "获取后台仪表盘文章发布热点统计信息")
    @ApiOperationLog(description = "获取后台仪表盘文章发布热点统计信息")
    @RequiresPermission(PermissionConstants.Dashboard.PUBLISH_ARTICLE_STATISTICS)
    public Response findDashboardPublishArticleStatistics() {
        return dashboardService.findDashboardPublishArticleStatistics();
    }

    @PostMapping("/pv/statistics")
    @Operation(description = "获取后台仪表盘最近一周 PV 访问量信息")
    @ApiOperationLog(description = "获取后台仪表盘最近一周 PV 访问量信息")
    @RequiresPermission(PermissionConstants.Dashboard.PV_STATISTICS)
    public Response findDashboardPVStatistics() {
        return dashboardService.findDashboardPVStatistics();
    }
}
