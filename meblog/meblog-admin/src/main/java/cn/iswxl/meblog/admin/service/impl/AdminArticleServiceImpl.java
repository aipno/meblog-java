package cn.iswxl.meblog.admin.service.impl;

import cn.iswxl.meblog.admin.convert.ArticleDetailConvert;
import cn.iswxl.meblog.search.event.DeleteArticleEvent;
import cn.iswxl.meblog.search.event.PublishArticleEvent;
import cn.iswxl.meblog.search.event.UpdateArticleEvent;
import cn.iswxl.meblog.admin.model.vo.article.*;
import cn.iswxl.meblog.admin.service.AdminArticleService;
import cn.iswxl.meblog.common.domain.dos.*;
import cn.iswxl.meblog.common.domain.mapper.*;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.common.markdown.MarkdownHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminArticleServiceImpl implements AdminArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ArticlePermissionMapper articlePermissionMapper;
    @Autowired
    private ImageMapper imageMapper;

    /**
     * 发布文章
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"articleDetails", "indexArticles"}, allEntries = true)
    public Response publishArticle(PublishArticleReqVO publishArticleReqVO) {
        // 分离图片并插入数据库图片表
        Long imageId = imageMapper.insertImage(publishArticleReqVO.getCover());
        // 1. VO 转 ArticleDO, 并保存
        ArticleDO articleDO = ArticleDO.builder()
                .title(publishArticleReqVO.getTitle())
                .coverId(imageId)
                .summary(publishArticleReqVO.getSummary())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        articleMapper.insert(articleDO);

        // 拿到插入记录的主键 ID
        Long articleId = articleDO.getId();

        // 将 Markdown 转换为 HTML
        String contentHtml = MarkdownHelper.convertMarkdown2Html(publishArticleReqVO.getContent());

        // 2. VO 转 ArticleContentDO，并保存
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(publishArticleReqVO.getContent())
                .contentHtml(contentHtml)
                .build();
        articleContentMapper.insert(articleContentDO);

        // 2.1 处理文章权限
        articlePermissionMapper.addPermission(articleId);

        // 3. 处理文章关联的分类
        Long categoryId = publishArticleReqVO.getCategoryId();

        // 3.1 校验提交的分类是否真实存在
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 4. 保存文章关联的标签集合
        List<String> publishTags = publishArticleReqVO.getTags();
        insertTags(articleId, publishTags);

        // 发送文章发布事件
        eventPublisher.publishEvent(new PublishArticleEvent(this, articleId));

        return Response.success();
    }

    /**
     * 保存标签
     */
    private void insertTags(Long articleId, List<String> publishTags) {
        // 如果没有标签直接返回
        if (CollectionUtils.isEmpty(publishTags)) {
            return;
        }

        // 只查询提交的标签名称对应的数据
        List<TagDO> existedTagDOs = tagMapper.selectList(Wrappers.<TagDO>lambdaQuery().in(TagDO::getId, publishTags));

        // 已存在的标签名称集合（转为小写以便比较）
        Map<String, TagDO> existedTagNameMap = existedTagDOs.stream()
                .collect(Collectors.toMap(tag -> tag.getName().toLowerCase(), tag -> tag, (existing, replacement) -> existing));

        // 分离已存在和不存在的标签
        List<String> notExistTags = new ArrayList<>();
        for (String tagName : publishTags) {
            // 检查标签是否已存在（忽略大小写）
            if (!existedTagNameMap.containsKey(tagName.toLowerCase())) {
                notExistTags.add(tagName);
            }
        }

        // 将提交的上来的，已存在于表中的标签，文章-标签关联关系入库
        if (!CollectionUtils.isEmpty(existedTagDOs)) {
            List<ArticleTagRelDO> articleTagRelDOS = existedTagDOs.stream()
                    .map(tagDO -> ArticleTagRelDO.builder()
                            .articleId(articleId)
                            .tagId(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
            // 批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }

        // 将提交的上来的，不存在于表中的标签，入库保存
        if (!CollectionUtils.isEmpty(notExistTags)) {
            // 需要先将标签入库，拿到对应标签 ID 后，再把文章-标签关联关系入库
            List<ArticleTagRelDO> articleTagRelDOS = notExistTags.stream()
                    .map(tagName -> {
                        TagDO tagDO = TagDO.builder()
                                .name(tagName)
                                .createTime(LocalDateTime.now())
                                .updateTime(LocalDateTime.now())
                                .build();

                        tagMapper.insert(tagDO);

                        // 文章-标签关联关系
                        return ArticleTagRelDO.builder()
                                .articleId(articleId)
                                .tagId(tagDO.getId())
                                .build();
                    })
                    .collect(Collectors.toList());

            // 批量插入
            articleTagRelMapper.insertBatchSomeColumn(articleTagRelDOS);
        }
    }

    /**
     * 删除文章
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"articleDetails", "indexArticles"}, allEntries = true)
    public Response deleteArticle(DeleteArticleReqVO deleteArticleReqVO) {
        Long articleId = deleteArticleReqVO.getId();

        // 1. 删除文章
        articleMapper.deleteById(articleId);

        // 2. 删除文章内容
        articleContentMapper.deleteByArticleId(articleId);

        // 3. 删除文章-分类关联记录
        articleCategoryRelMapper.deleteByArticleId(articleId);

        // 4. 删除文章-标签关联记录
        articleTagRelMapper.deleteByArticleId(articleId);

        // 5. 删除文章权限
        articlePermissionMapper.deletePermission(articleId);

        // 发布文章删除事件
        eventPublisher.publishEvent(new DeleteArticleEvent(this, articleId));

        return Response.success();
    }

    /**
     * 查询文章分页数据
     */
    @Override
    public Response findArticlePageList(FindArticlePageListReqVO findArticlePageListReqVO) {
        // 获取当前页、以及每页需要展示的数据数量
        Long current = findArticlePageListReqVO.getCurrent();
        Long size = findArticlePageListReqVO.getSize();
        String title = findArticlePageListReqVO.getTitle();
        LocalDate startDate = findArticlePageListReqVO.getStartDate();
        LocalDate endDate = findArticlePageListReqVO.getEndDate();
        Integer type = findArticlePageListReqVO.getType();
        Integer IsPublish = findArticlePageListReqVO.getIsPublish();

        // 执行分页查询
        Page<ArticleDO> articleDOPage = articleMapper.selectPageList(current, size, title, startDate, endDate, type, IsPublish);

        List<ArticleDO> articleDOS = articleDOPage.getRecords();


        // DO 转 VO
        List<FindArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(articleDO -> {
                        Boolean isPermission = articlePermissionMapper.isPermission(articleDO.getId());
                        String cover = imageMapper.selectById(articleDO.getCoverId()).getImageUrl();
                        return FindArticlePageListRspVO.builder()
                                .id(articleDO.getId())
                                .title(articleDO.getTitle())
                                .cover(cover)
                                .createTime(articleDO.getCreateTime())
                                .isPublish(articleDO.getIsPublish())
                                .isPermission(isPermission)
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return PageResponse.success(articleDOPage, vos);
    }

    /**
     * 查询文章详情
     */
    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        Long articleId = findArticleDetailReqVO.getId();

        ArticleDO articleDO = articleMapper.selectById(articleId);

        if (Objects.isNull(articleDO)) {
            log.warn("==> 查询的文章不存在，articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);

        // 所属分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);

        // 对应标签
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        // 获取对应标签 ID 集合
        List<Long> tagIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());

        // DO 转 VO
        FindArticleDetailRspVO vo = ArticleDetailConvert.INSTANCE.convertDO2VO(articleDO);
        vo.setContent(articleContentDO.getContent());
        vo.setCover(String.valueOf(articleDO.getCoverId()));
        vo.setCoverUrl(imageMapper.selectById(articleDO.getCoverId()).getImageUrl());
        vo.setCategoryId(articleCategoryRelDO.getCategoryId());
        vo.setTagIds(tagIds);

        return Response.success(vo);
    }

    /**
     * 更新文章
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"articleDetails", "indexArticles"}, allEntries = true)
    public Response updateArticle(UpdateArticleReqVO updateArticleReqVO) {
        Long articleId = updateArticleReqVO.getId();
        Long imageId = imageMapper.insertImage(updateArticleReqVO.getCover());

        // 1. VO 转 ArticleDO, 并更新
        ArticleDO articleDO = ArticleDO.builder()
                .id(articleId)
                .title(updateArticleReqVO.getTitle())
                .coverId(imageId)
                .summary(updateArticleReqVO.getSummary())
                .updateTime(LocalDateTime.now())
                .build();
        int count = articleMapper.updateById(articleDO);

        // 根据更新是否成功，来判断该文章是否存在
        if (count == 0) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 将 Markdown 转换为 HTML
        String contentHtml = MarkdownHelper.convertMarkdown2Html(updateArticleReqVO.getContent());

        // 2. VO 转 ArticleContentDO，并更新
        ArticleContentDO articleContentDO = ArticleContentDO.builder()
                .articleId(articleId)
                .content(updateArticleReqVO.getContent())
                .contentHtml(contentHtml)
                .build();
        articleContentMapper.updateByArticleId(articleContentDO);


        // 3. 更新文章分类
        Long categoryId = updateArticleReqVO.getCategoryId();

        // 3.1 校验提交的分类是否真实存在
        CategoryDO categoryDO = categoryMapper.selectById(categoryId);
        if (Objects.isNull(categoryDO)) {
            log.warn("==> 分类不存在, categoryId: {}", categoryId);
            throw new BizException(ResponseCodeEnum.CATEGORY_NOT_EXISTED);
        }

        // 先删除该文章关联的分类记录，再插入新的关联关系
        articleCategoryRelMapper.deleteByArticleId(articleId);
        ArticleCategoryRelDO articleCategoryRelDO = ArticleCategoryRelDO.builder()
                .articleId(articleId)
                .categoryId(categoryId)
                .build();
        articleCategoryRelMapper.insert(articleCategoryRelDO);

        // 4. 保存文章关联的标签集合
        // 先删除该文章对应的标签
        articleTagRelMapper.deleteByArticleId(articleId);
        List<String> publishTags = updateArticleReqVO.getTags();
        insertTags(articleId, publishTags);

        // 发布文章修改事件
        eventPublisher.publishEvent(new UpdateArticleEvent(this, articleId));

        return Response.success();
    }

    /**
     * 修改文章是否发布
     */
    @Override
    @CacheEvict(value = {"articleDetails", "indexArticles"}, allEntries = true)
    public Response updateArticleIsPublish(UpdateArticleIsPublishReqVO updateArticleIsPublishReqVO) {
        Long articleId = updateArticleIsPublishReqVO.getId();
        Boolean isPublish = updateArticleIsPublishReqVO.getIsPublish();
        // 更新发布状态
        articleMapper.updateById(ArticleDO.builder().id(articleId).isPublish(isPublish).build());
        return Response.success();
    }

    /**
     * 修改文章权限
     */
    @Override
    @CacheEvict(value = {"articleDetails", "indexArticles"}, allEntries = true)
    public Response updateArticleIsPermission(UpdateArticlePermissionReqVO updateArticlePermissionReqVO) {
        Long articleId = updateArticlePermissionReqVO.getId();
        Boolean isPermission = updateArticlePermissionReqVO.getIsPermission();
        // 更新文章权限状态
        articlePermissionMapper.updatePermission(articleId, isPermission);
        return Response.success();
    }

}
