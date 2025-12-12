package cn.iswxl.meblog.web.service.impl;

import cn.iswxl.meblog.common.domain.dos.ArticleCategoryRelDO;
import cn.iswxl.meblog.common.domain.dos.ArticleDO;
import cn.iswxl.meblog.common.domain.dos.CategoryDO;
import cn.iswxl.meblog.common.domain.dos.ImageDO;
import cn.iswxl.meblog.common.domain.mapper.ArticleCategoryRelMapper;
import cn.iswxl.meblog.common.domain.mapper.ArticleMapper;
import cn.iswxl.meblog.common.domain.mapper.CategoryMapper;
import cn.iswxl.meblog.common.domain.mapper.ImageMapper;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.web.convert.ArticleConvert;
import cn.iswxl.meblog.web.model.vo.category.FindCategoryArticlePageListReqVO;
import cn.iswxl.meblog.web.model.vo.category.FindCategoryArticlePageListRspVO;
import cn.iswxl.meblog.web.model.vo.category.FindCategoryListReqVO;
import cn.iswxl.meblog.web.model.vo.category.FindCategoryListRspVO;
import cn.iswxl.meblog.web.service.CategoryService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ImageMapper imageMapper;

    /**
     * 获取分类列表
     *
     * @return
     */
    @Override
    public Response findCategoryList(FindCategoryListReqVO findCategoryListReqVO) {
        Long size = findCategoryListReqVO.getSize();

        List<CategoryDO> categoryDOS = null;
        // 如果接口入参中未指定 size
        if (Objects.isNull(size) || size == 0) {
            // 查询所有分类
            categoryDOS = categoryMapper.selectList(Wrappers.emptyWrapper());
        } else {
            // 否则查询指定的数量
            categoryDOS = categoryMapper.selectByLimit(size);
        }

        // DO 转 VO
        List<FindCategoryListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(categoryDOS)) {
            vos = categoryDOS.stream()
                    .map(categoryDO -> FindCategoryListRspVO.builder()
                            .id(categoryDO.getId())
                            .name(categoryDO.getName())
                            .articlesTotal(categoryDO.getArticlesTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    /**
     * 获取分类下文章分页数据
     *
     * @param findCategoryArticlePageListReqVO
     * @return
     */
    @Override
    public Response findCategoryArticlePageList(FindCategoryArticlePageListReqVO findCategoryArticlePageListReqVO) {
        Long current = findCategoryArticlePageListReqVO.getCurrent();
        Long size = findCategoryArticlePageListReqVO.getSize();
        Long categoryId = findCategoryArticlePageListReqVO.getId();

        CategoryDO categoryDO = categoryMapper.selectById(categoryId);

        // 判断该分类是否存在
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 该分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        // 先查询该分类下所有关联的文章 ID
        List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectListByCategoryId(categoryId);

        // 若该分类下未发布任何文章
        if (CollectionUtils.isEmpty(articleCategoryRelDOS)) {
            log.info("==> 该分类下还未发布任何文章, categoryId: {}", categoryId);
            return PageResponse.success(null, null);
        }

        List<Long> articleIds = articleCategoryRelDOS.stream().map(ArticleCategoryRelDO::getArticleId).collect(Collectors.toList());

        // 根据文章 ID 集合查询文章分页数据（在数据库层面过滤掉未发布和非普通类型的文章）
        Page<ArticleDO> page = articleMapper.selectPageListByArticleIds(current, size, articleIds, 1, 1); // type=1表示普通文章，isPublish=1表示已发布
        List<ArticleDO> articleDOS = page.getRecords();

        // DO 转 VO
        List<FindCategoryArticlePageListRspVO> vos = Collections.emptyList();
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(ArticleConvert.INSTANCE::convertDO2CategoryArticleVO)
                    .collect(Collectors.toList());
        }

        // 设置文章封面图片
        List<ArticleDO> finalArticleDOS = articleDOS;
        // 只有当 vos 不为空时才执行 forEach 操作
        if (!CollectionUtils.isEmpty(vos)) {
            vos.forEach(vo -> {
                // 通过文章ID找到对应的文章DO对象
                Optional<ArticleDO> articleOptional = finalArticleDOS.stream()
                        .filter(article -> Objects.equals(article.getId(), vo.getId()))
                        .findFirst();

                if (articleOptional.isPresent()) {
                    ArticleDO articleDO = articleOptional.get();
                    if (articleDO.getCoverId() != null) {
                        ImageDO imageDO = imageMapper.selectById(articleDO.getCoverId());
                        if (imageDO != null) {
                            vo.setCover(imageDO.getImageUrl());
                        }
                    }
                }
            });
        }

        return PageResponse.success(page, vos);
    }
}

