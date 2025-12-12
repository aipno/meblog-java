package cn.iswxl.meblog.admin.service;

import cn.iswxl.meblog.admin.model.vo.article.*;
import cn.iswxl.meblog.admin.model.vo.wiki.UpdateWikiIsPublishReqVO;
import cn.iswxl.meblog.common.utils.Response;

public interface AdminArticleService {
    /**
     * 发布文章
     * @param publishArticleReqVO
     * @return
     */
    Response publishArticle(PublishArticleReqVO publishArticleReqVO);

    /**
     * 删除文章
     * @param deleteArticleReqVO
     * @return
     */
    Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO);

    /**
     * 查询文章分页数据
     * @param findArticlePageListReqVO
     * @return
     */
    Response findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO);

    /**
     * 查询文章详情
     * @param findArticleDetailReqVO
     * @return
     */
    Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO);

    /**
     * 更新文章
     * @param updateArticleReqVO
     * @return
     */
    Response updateArticle(UpdateArticleReqVO updateArticleReqVO);

    /**
     * 更新文章发布状态
     * @param updateWikiIsPublishReqVO
     * @return
     */
    Response updateArticleIsPublish(UpdateArticleIsPublishReqVO updateArticleIsPublishReqVO);

    /**
     * 更新文章权限
     * @return
     */
    Response updateArticleIsPermission(UpdateArticlePermissionReqVO updateArticlePermissionReqVO);
}

