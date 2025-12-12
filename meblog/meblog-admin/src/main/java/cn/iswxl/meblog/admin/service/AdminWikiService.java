package cn.iswxl.meblog.admin.service;

import cn.iswxl.meblog.admin.model.vo.wiki.*;
import cn.iswxl.meblog.common.utils.Response;

public interface AdminWikiService {

    /**
     * 新增知识库
     * @param addWiKiReqVO
     * @return
     */
    Response addWiKi(AddWikiReqVO addWiKiReqVO);

    /**
     * 删除知识库
     * @param deleteWiKiReqVO
     * @return
     */
    Response deleteWiKi(DeleteWikiReqVO deleteWiKiReqVO);

    /**
     * 知识库分页查询
     * @param findWikiPageListReqVO
     * @return
     */
    Response findWikiPageList(FindWikiPageListReqVO findWikiPageListReqVO);

    /**
     * 更新知识库置顶状态
     * @param updateWikiIsTopReqVO
     * @return
     */
    Response updateWikiIsTop(UpdateWikiIsTopReqVO updateWikiIsTopReqVO);

    /**
     * 更新知识库发布状态
     * @param updateWikiIsPublishReqVO
     * @return
     */
    Response updateWikiIsPublish(UpdateWikiIsPublishReqVO updateWikiIsPublishReqVO);

    /**
     * 更新知识库
     * @param updateWikiReqVO
     * @return
     */
    Response updateWiki(UpdateWikiReqVO updateWikiReqVO);

    /**
     * 查询知识库目录
     * @param findWikiCatalogListReqVO
     * @return
     */
    Response findWikiCatalogList(FindWikiCatalogListReqVO findWikiCatalogListReqVO);

    /**
     * 更新知识库目录
     * @param updateWikiCatalogReqVO
     * @return
     */
    Response updateWikiCatalogs(UpdateWikiCatalogReqVO updateWikiCatalogReqVO);
}
