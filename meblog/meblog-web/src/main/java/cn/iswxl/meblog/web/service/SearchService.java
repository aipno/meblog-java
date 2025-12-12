package cn.iswxl.meblog.web.service;

import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.web.model.vo.search.SearchArticlePageListReqVO;

public interface SearchService {
    /**
     * 关键词分页搜索
     * @param searchArticlePageListReqVO
     * @return
     */
    Response searchArticlePageList(SearchArticlePageListReqVO searchArticlePageListReqVO);
}
