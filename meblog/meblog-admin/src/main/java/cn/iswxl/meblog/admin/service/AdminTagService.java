package cn.iswxl.meblog.admin.service;

import cn.iswxl.meblog.admin.model.vo.tag.*;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;

public interface AdminTagService {

    /**
     * 添加标签集合
     */
    Response addTags(AddTagReqVO addCategoryReqVO);

    /**
     * 查询标签分页
     */
    PageResponse findTagPageList(FindTagPageListReqVO findTagPageListReqVO);

    /**
     * 删除标签
     */
    Response deleteTag(DeleteTagReqVO deleteTagReqVO);

    /**
     * 根据标签关键词模糊查询
     */
    Response searchTag(SearchTagReqVO searchTagReqVO);

    /**
     * 查询标签 Select 列表数据
     */
    Response findTagSelectList();

    /**
     * 更新标签
     */
    Response updateTag(UpdateTagReqVO updateTagReqVO);
}
