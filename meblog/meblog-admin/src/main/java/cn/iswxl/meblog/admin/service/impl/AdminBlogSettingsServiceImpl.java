package cn.iswxl.meblog.admin.service.impl;

import cn.iswxl.meblog.admin.convert.BlogSettingsConvert;
import cn.iswxl.meblog.admin.model.vo.blogsettings.FindBlogSettingsRspVO;
import cn.iswxl.meblog.admin.model.vo.blogsettings.UpdateBlogSettingsReqVO;
import cn.iswxl.meblog.admin.service.AdminBlogSettingsService;
import cn.iswxl.meblog.common.domain.dos.BlogSettingsDO;
import cn.iswxl.meblog.common.domain.mapper.BlogSettingsMapper;
import cn.iswxl.meblog.common.utils.Response;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminBlogSettingsServiceImpl extends ServiceImpl<BlogSettingsMapper, BlogSettingsDO> implements AdminBlogSettingsService {

    private static final Long BLOG_SETTINGS_ID = 1L;

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    @Override
    public Response updateBlogSettings(UpdateBlogSettingsReqVO updateBlogSettingsReqVO) {
        // VO 转 DO
        BlogSettingsDO blogSettingsDO = BlogSettingsConvert.INSTANCE.convertVO2DO(updateBlogSettingsReqVO);
        blogSettingsDO.setId(BLOG_SETTINGS_ID);

        // 保存或更新（当数据库中存在 ID 为 1 的记录时，则执行更新操作，否则执行插入操作）
        saveOrUpdate(blogSettingsDO);
        return Response.success();
    }

    /**
     * 获取博客设置详情
     *
     * @return
     */
    @Override
    public Response findDetail() {
        // 查询 ID 为 1 的记录
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(BLOG_SETTINGS_ID);

        // DO 转 VO
        FindBlogSettingsRspVO vo = BlogSettingsConvert.INSTANCE.convertDO2VO(blogSettingsDO);

        return Response.success(vo);
    }
}


