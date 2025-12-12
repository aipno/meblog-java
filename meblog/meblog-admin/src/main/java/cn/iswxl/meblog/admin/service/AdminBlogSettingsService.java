package cn.iswxl.meblog.admin.service;

import cn.iswxl.meblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import cn.iswxl.meblog.common.utils.Response;

public interface AdminBlogSettingsService {
    /**
     * 更新博客设置信息
     * @param updateBlogSettingsReqVO
     * @return
     */
    Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO);

    /**
     * 获取博客设置详情
     * @return
     */
    Response findDetail();
}

