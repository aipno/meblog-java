package cn.iswxl.meblog.web.service;

import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.web.model.vo.article.FindArticleDetailReqVO;
import cn.iswxl.meblog.web.model.vo.article.FindIndexArticlePageListReqVO;

public interface ArticleService {
    /**
     * 获取首页文章分页数据
     * @param findIndexArticlePageListReqVO
     * @return
     */
    Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO);

    /**
     * 获取文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

}

