package cn.iswxl.meblog.common.service.impl;

import cn.iswxl.meblog.common.service.AdminStatisticsService;
import cn.iswxl.meblog.common.domain.dos.ArticleCategoryRelDO;
import cn.iswxl.meblog.common.domain.dos.ArticleTagRelDO;
import cn.iswxl.meblog.common.domain.dos.CategoryDO;
import cn.iswxl.meblog.common.domain.dos.TagDO;
import cn.iswxl.meblog.common.domain.mapper.ArticleCategoryRelMapper;
import cn.iswxl.meblog.common.domain.mapper.ArticleTagRelMapper;
import cn.iswxl.meblog.common.domain.mapper.CategoryMapper;
import cn.iswxl.meblog.common.domain.mapper.TagMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Override
    public void statisticsCategoryArticleTotal() {
        // 查询所有分类
        List<CategoryDO> categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());

        if (!CollectionUtils.isEmpty(categoryDOS)) {
            // 循环统计各分类下的文章总数，使用数据库 COUNT 查询替代全表加载
            for (CategoryDO categoryDO : categoryDOS) {
                Long categoryId = categoryDO.getId();

                // 直接在数据库层面统计该分类下的文章数量
                int articlesTotal = articleCategoryRelMapper.selectCount(Wrappers.<ArticleCategoryRelDO>lambdaQuery()
                        .eq(ArticleCategoryRelDO::getCategoryId, categoryId)).intValue();

                // 更新该分类的文章总数
                CategoryDO categoryDO1 = CategoryDO.builder()
                        .id(categoryId)
                        .articlesTotal(articlesTotal)
                        .build();
                categoryMapper.updateById(categoryDO1);
            }
        }
    }

    /**
     * 统计各标签下文章总数
     */
    @Override
    public void statisticsTagArticleTotal() {
        // 查询所有标签
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        if (!CollectionUtils.isEmpty(tagDOS)) {
            // 循环统计各标签下的文章总数，使用数据库 COUNT 查询替代全表加载
            for (TagDO tagDO : tagDOS) {
                Long tagId = tagDO.getId();

                // 直接在数据库层面统计该标签下的文章数量
                int articlesTotal = articleTagRelMapper.selectCount(Wrappers.<ArticleTagRelDO>lambdaQuery()
                        .eq(ArticleTagRelDO::getTagId, tagId)).intValue();

                // 更新该标签的文章总数
                TagDO tagDO1 = TagDO.builder()
                        .id(tagId)
                        .articlesTotal(articlesTotal)
                        .build();
                tagMapper.updateById(tagDO1);
            }
        }
    }
}
