package cn.iswxl.meblog.web.controller;

import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.web.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@Tag(name = "统计信息")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/info")
    @Operation(description = "前台获取统计信息")
    @ApiOperationLog(description = "前台获取统计信息")
    public Response findInfo() {
        return statisticsService.findInfo();
    }

}
