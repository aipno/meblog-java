package cn.iswxl.meblog.admin.service;

import cn.iswxl.meblog.admin.model.vo.category.*;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;

public interface AdminCategoryService {
    /**
     * 添加分类
     */
    Response addCategory(AddCategoryReqVO addCategoryReqVO);

    /**
     * 分类分页数据查询
     */
    PageResponse<FindCategoryPageListRspVO> findCategoryPageList(FindCategoryPageListReqVO findCategoryPageListReqVO);

    /**
     * 删除分类
     */
    Response deleteCategory(DeleteCategoryReqVO deleteCategoryReqVO);

    /**
     * 获取文章分类的 Select 列表数据
     */
    Response findCategorySelectList();

    /**
     * 分类更新
     */
    Response updateCategory(UpdateCategoryReqVO updateCategoryReqVO);
}
