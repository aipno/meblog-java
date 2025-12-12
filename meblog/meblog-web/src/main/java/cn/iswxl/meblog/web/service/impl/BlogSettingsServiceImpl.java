package cn.iswxl.meblog.web.service.impl;

import cn.iswxl.meblog.common.domain.dos.BlogSettingsDO;
import cn.iswxl.meblog.common.domain.mapper.BlogSettingsMapper;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.web.convert.BlogSettingsConvert;
import cn.iswxl.meblog.web.model.vo.blogsettings.FindBlogSettingsDetailRspVO;
import cn.iswxl.meblog.web.service.BlogSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BlogSettingsServiceImpl implements BlogSettingsService {

    private static final Long BLOG_SETTINGS_ID = 1L;

    @Autowired
    private BlogSettingsMapper blogSettingsMapper;

    /**
     * 获取博客设置信息
     *
     * @return
     */
    @Override
    public Response findDetail() {
        return findDetailWithCache();
    }
    
    /**
     * 带缓存的博客设置详情查询
     * @return
     */
    @Cacheable(value = "blogSettings", key = "'blogSetting'")
    public Response findDetailWithCache() {
        // 查询博客设置信息（约定的 ID 为 1）
        BlogSettingsDO blogSettingsDO = blogSettingsMapper.selectById(BLOG_SETTINGS_ID);
        // DO 转 VO
        FindBlogSettingsDetailRspVO vo = BlogSettingsConvert.INSTANCE.convertDO2VO(blogSettingsDO);

        return Response.success(vo);
    }
}
