package cn.iswxl.meblog.admin.service;

import cn.iswxl.meblog.admin.model.vo.tag.AddTagReqVO;
import cn.iswxl.meblog.admin.model.vo.tag.DeleteTagReqVO;
import cn.iswxl.meblog.admin.model.vo.tag.FindTagPageListReqVO;
import cn.iswxl.meblog.admin.model.vo.tag.SearchTagReqVO;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;

public interface AdminTagService {

    /**
     * 添加标签集合
     * @param addCategoryReqVO
     * @return
     */
    Response addTags(AddTagReqVO addCategoryReqVO);

    /**
     * 查询标签分页
     * @param findTagPageListReqVO
     * @return
     */
    PageResponse findTagPageList(FindTagPageListReqVO findTagPageListReqVO);

    /**
     * 删除标签
     * @param deleteTagReqVO
     * @return
     */
    Response deleteTag(DeleteTagReqVO deleteTagReqVO);

    /**
     * 根据标签关键词模糊查询
     * @param searchTagReqVO
     * @return
     */
    Response searchTag(SearchTagReqVO searchTagReqVO);

    /**
     * 查询标签 Select 列表数据
     * @return
     */
    Response findTagSelectList();
}
