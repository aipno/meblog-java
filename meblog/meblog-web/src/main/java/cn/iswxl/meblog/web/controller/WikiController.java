package cn.iswxl.meblog.web.controller;

import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.jwt.annotation.RequiresPermission;
import cn.iswxl.meblog.web.model.vo.wiki.FindWikiArticlePreNextReqVO;
import cn.iswxl.meblog.web.model.vo.wiki.FindWikiCatalogListReqVO;
import cn.iswxl.meblog.web.service.WikiService;
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
@RequestMapping("/wiki")
@Tag(name = "知识库")
public class WikiController {

    @Autowired
    private WikiService wikiService;

    @PostMapping("/list")
    @Operation(description = "获取知识库数据")
    @ApiOperationLog(description = "获取知识库数据")
    @RequiresPermission(value = "web:wiki:list")
    public Response findWikiList() {
        return wikiService.findWikiList();
    }

    @PostMapping("/catalog/list")
    @Operation(description = "获取知识库目录数据")
    @ApiOperationLog(description = "获取知识库目录数据")
    @RequiresPermission(value = "web:wiki:catalog:list")
    public Response findWikiCatalogList(@RequestBody @Validated FindWikiCatalogListReqVO findWikiCatalogListReqVO) {
        return wikiService.findWikiCatalogList(findWikiCatalogListReqVO);
    }

    @PostMapping("/article/preNext")
    @Operation(description = "获取知识库文章上下页")
    @ApiOperationLog(description = "获取知识库文章上下页")
    public Response findArticlePreNext(@RequestBody FindWikiArticlePreNextReqVO findWikiArticlePreNextReqVO) {
        return wikiService.findArticlePreNext(findWikiArticlePreNextReqVO);
    }
}
