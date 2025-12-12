package cn.iswxl.meblog.web.service.impl;

import cn.iswxl.meblog.common.domain.dos.*;
import cn.iswxl.meblog.common.domain.mapper.*;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.search.event.ReadArticleEvent;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.common.markdown.MarkdownHelper;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.web.convert.ArticleConvert;
import cn.iswxl.meblog.web.model.vo.archive.FindPreNextArticleRspVO;
import cn.iswxl.meblog.web.model.vo.article.FindArticleDetailReqVO;
import cn.iswxl.meblog.web.model.vo.article.FindArticleDetailRspVO;
import cn.iswxl.meblog.web.model.vo.article.FindIndexArticlePageListReqVO;
import cn.iswxl.meblog.web.model.vo.article.FindIndexArticlePageListRspVO;
import cn.iswxl.meblog.web.model.vo.category.FindCategoryListRspVO;
import cn.iswxl.meblog.web.model.vo.tag.FindTagListRspVO;
import cn.iswxl.meblog.web.service.ArticleService;
import cn.iswxl.meblog.web.utils.MarkdownStatsUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    // Redis中存储热门文章ID的Sorted Set的key
    private static final String HOT_ARTICLES_KEY = "hot_articles";
    // 热门文章数量上限
    private static final int HOT_ARTICLES_LIMIT = 100;
    // 成为热门文章的最低访问次数阈值
    private static final int HOT_ARTICLES_THRESHOLD = 10;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleCategoryRelMapper articleCategoryRelMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ArticlePermissionMapper articlePermissionMapper;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取首页文章分页数据
     *
     * @param findIndexArticlePageListReqVO
     * @return
     */
    @Override
    public Response findArticlePageList(FindIndexArticlePageListReqVO findIndexArticlePageListReqVO) {
        Long current = findIndexArticlePageListReqVO.getCurrent();
        Long size = findIndexArticlePageListReqVO.getSize();

        // 第一步：分页查询文章主体记录
        Page<ArticleDO> articleDOPage = articleMapper.selectPageList(current, size, null, null, null, 1, 1);
        // 返回的分页数据
        List<ArticleDO> articleDOS = articleDOPage.getRecords();

        List<FindIndexArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            // 文章 DO 转 VO
            vos = articleDOS.stream()
                    .map(ArticleConvert.INSTANCE::convertDO2VO)
                    .collect(Collectors.toList());

            // 拿到所有文章的 ID 集合
            List<Long> articleIds = articleDOS.stream()
                    .map(ArticleDO::getId)
                    .collect(Collectors.toList());

            // 第二步：设置文章所属分类
            // 根据文章 ID 批量查询所有关联记录
            List<ArticleCategoryRelDO> articleCategoryRelDOS = articleCategoryRelMapper.selectByArticleIds(articleIds);

            // 只查询当前页文章涉及的分类
            List<Long> categoryIds = articleCategoryRelDOS.stream()
                    .map(ArticleCategoryRelDO::getCategoryId)
                    .distinct()
                    .collect(Collectors.toList());

            List<CategoryDO> categoryDOS = Collections.emptyList();
            Map<Long, String> categoryIdNameMap;
            if (!categoryIds.isEmpty()) {
                categoryDOS = categoryMapper.selectBatchIds(categoryIds);
                // 转 Map, 方便后续根据分类 ID 拿到对应的分类名称
                categoryIdNameMap = categoryDOS.stream().collect(Collectors.toMap(CategoryDO::getId, CategoryDO::getName));
            } else {
                categoryIdNameMap = Collections.emptyMap();
            }

            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 过滤出当前文章对应的关联数据
                Optional<ArticleCategoryRelDO> optional = articleCategoryRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).findAny();

                // 若不为空
                if (optional.isPresent()) {
                    ArticleCategoryRelDO articleCategoryRelDO = optional.get();
                    Long categoryId = articleCategoryRelDO.getCategoryId();
                    // 通过分类 ID 从 map 中拿到对应的分类名称
                    String categoryName = categoryIdNameMap.get(categoryId);

                    FindCategoryListRspVO findCategoryListRspVO = FindCategoryListRspVO.builder()
                            .id(categoryId)
                            .name(categoryName)
                            .build();
                    // 设置到当前 vo 类中
                    vo.setCategory(findCategoryListRspVO);
                }
            });

            // 第三步：设置文章标签
            // 拿到所有文章的标签关联记录
            List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleIds(articleIds);

            // 只查询当前页文章涉及的标签
            List<Long> tagIds = articleTagRelDOS.stream()
                    .map(ArticleTagRelDO::getTagId)
                    .distinct()
                    .collect(Collectors.toList());

            List<TagDO> tagDOS = Collections.emptyList();
            Map<Long, String> mapIdNameMap;
            if (!tagIds.isEmpty()) {
                tagDOS = tagMapper.selectBatchIds(tagIds);
                // 转 Map, 方便后续根据标签 ID 拿到对应的标签名称
                mapIdNameMap = tagDOS.stream().collect(Collectors.toMap(TagDO::getId, TagDO::getName));
            } else {
                mapIdNameMap = Collections.emptyMap();
            }

            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 过滤出当前文章的标签关联记录
                List<ArticleTagRelDO> articleTagRelDOList = articleTagRelDOS.stream().filter(rel -> Objects.equals(rel.getArticleId(), currArticleId)).collect(Collectors.toList());

                List<FindTagListRspVO> findTagListRspVOS = new ArrayList<>();
                // 将关联记录 DO 转 VO, 并设置对应的标签名称
                articleTagRelDOList.forEach(articleTagRelDO -> {
                    Long tagId = articleTagRelDO.getTagId();
                    String tagName = mapIdNameMap.get(tagId);

                    FindTagListRspVO findTagListRspVO = FindTagListRspVO.builder()
                            .id(tagId)
                            .name(tagName)
                            .build();
                    findTagListRspVOS.add(findTagListRspVO);
                });
                // 设置转换后的标签数据
                vo.setTags(findTagListRspVOS);
            });

            // 第四步：设置文章封面
            // 收集所有需要查询的coverId
            Set<Long> coverIds = articleDOS.stream()
                    .filter(article -> article.getCoverId() != null)
                    .map(ArticleDO::getCoverId)
                    .collect(Collectors.toSet());

            // 批量查询所有图片信息
            Map<Long, ImageDO> imageMap;
            if (!coverIds.isEmpty() && imageMapper != null) {
                List<ImageDO> imageList = imageMapper.selectBatchIds(coverIds);
                imageMap = imageList.stream()
                        .collect(Collectors.toMap(ImageDO::getId, image -> image));
            } else {
                imageMap = new HashMap<>();
            }

            // 设置文章封面
            vos.forEach(vo -> {
                Long currArticleId = vo.getId();
                // 通过文章ID找到对应的文章DO对象，从中获取coverId
                articleDOS.stream()
                        .filter(article -> Objects.equals(article.getId(), currArticleId))
                        .findFirst()
                        .ifPresent(articleDO -> {
                            if (articleDO.getCoverId() != null) {
                                ImageDO imageDO = imageMap.get(articleDO.getCoverId());
                                if (imageDO != null) {
                                    vo.setCover(imageDO.getImageUrl());
                                }
                            }
                        });
            });
        }
        return PageResponse.success(articleDOPage, vos);
    }

    /**
     * 获取文章详情
     *
     * @param findArticleDetailReqVO
     * @return
     */
    @Override
    public Response findArticleDetail(FindArticleDetailReqVO findArticleDetailReqVO) {
        Long articleId = findArticleDetailReqVO.getArticleId();

        // 记录文章访问次数，用于统计热门文章
        recordArticleVisit(articleId);

        // 检查是否是热门文章，如果是则从缓存获取
        if (isHotArticle(articleId)) {
            // 从缓存获取热门文章详情
            Response cachedResponse = getCachedArticleDetail(articleId);
            if (cachedResponse != null) {
                return cachedResponse;
            }
        }

        // 从数据库获取文章详情
        ArticleDO articleDO = articleMapper.selectById(articleId);

        // 判断文章是否存在
        if (Objects.isNull(articleDO)) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        if (Boolean.TRUE.equals(articlePermissionMapper.isPermission(articleId))) {
            log.info("==> 用户没有阅读权限, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.UNAUTHORIZED);
        }

        // 查询正文
        ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);
        String content = articleContentDO.getContent();
        // 直接使用预先转换好的 HTML 内容
        String contentHtml = articleContentDO.getContentHtml();

        // 获取实时的阅读量（包括Redis缓冲中的）
        Long realTimeReadNum = getRealTimeReadNum(articleId, articleDO.getReadNum());

        // 计算 md 正文字数
        int totalWords = MarkdownStatsUtil.calculateWordCount(content);

        // DO 转 VO
        FindArticleDetailRspVO vo = FindArticleDetailRspVO.builder()
                .title(articleDO.getTitle())
                .createTime(articleDO.getCreateTime())
                .content(contentHtml != null ? contentHtml : MarkdownHelper.convertMarkdown2Html(content))
                .readNum(realTimeReadNum)
                .totalWords(totalWords)
                .readTime(MarkdownStatsUtil.calculateReadingTime(totalWords))
                .updateTime(articleDO.getUpdateTime())
                .build();

        // 查询所属分类
        ArticleCategoryRelDO articleCategoryRelDO = articleCategoryRelMapper.selectByArticleId(articleId);
        CategoryDO categoryDO = categoryMapper.selectById(articleCategoryRelDO.getCategoryId());
        vo.setCategoryId(categoryDO.getId());
        vo.setCategoryName(categoryDO.getName());

        // 查询标签
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByArticleId(articleId);
        List<Long> tagIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getTagId).collect(Collectors.toList());
        List<TagDO> tagDOS = tagMapper.selectByIds(tagIds);

        // 标签 DO 转 VO
        List<FindTagListRspVO> tagVOS = tagDOS.stream()
                .map(tagDO -> FindTagListRspVO.builder().id(tagDO.getId()).name(tagDO.getName()).build())
                .collect(Collectors.toList());
        vo.setTags(tagVOS);

        // 上一篇
        ArticleDO preArticleDO = articleMapper.selectPreArticle(articleId);
        if (Objects.nonNull(preArticleDO)) {
            FindPreNextArticleRspVO preArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(preArticleDO.getId())
                    .articleTitle(preArticleDO.getTitle())
                    .build();
            vo.setPreArticle(preArticleVO);
        }

        // 下一篇
        ArticleDO nextArticleDO = articleMapper.selectNextArticle(articleId);
        if (Objects.nonNull(nextArticleDO)) {
            FindPreNextArticleRspVO nextArticleVO = FindPreNextArticleRspVO.builder()
                    .articleId(nextArticleDO.getId())
                    .articleTitle(nextArticleDO.getTitle())
                    .build();
            vo.setNextArticle(nextArticleVO);
        }

        // 发布文章阅读事件
        eventPublisher.publishEvent(new ReadArticleEvent(this, articleId));

        // 如果是热门文章，缓存结果
        if (isHotArticle(articleId)) {
            cacheArticleDetail(articleId, Response.success(vo));
        }

        return Response.success(vo);
    }

    /**
     * 获取文章的实时阅读量（数据库值 + Redis缓冲值）
     *
     * @param articleId 文章ID
     * @param dbReadNum 数据库中的阅读量
     * @return 实时阅读量
     */
    private Long getRealTimeReadNum(Long articleId, Long dbReadNum) {
        try {
            String key = "article:read_count:" + articleId;
            Object redisReadCount = redisTemplate.opsForValue().get(key);

            if (redisReadCount != null) {
                Long bufferedReadCount = Long.parseLong(redisReadCount.toString());
                return dbReadNum + bufferedReadCount;
            }
        } catch (Exception e) {
            log.error("获取文章实时阅读量失败, articleId: {}", articleId, e);
        }
        return dbReadNum;
    }

    /**
     * 记录文章访问次数
     *
     * @param articleId
     */
    private void recordArticleVisit(Long articleId) {
        try {
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
            // 增加文章访问次数
            zSetOps.incrementScore(HOT_ARTICLES_KEY, String.valueOf(articleId), 1);

            // 限制热门文章数量，移除访问次数最少的文章
            Long size = zSetOps.size(HOT_ARTICLES_KEY);
            if (size != null && size > HOT_ARTICLES_LIMIT) {
                // 移除分数最低的项
                zSetOps.removeRange(HOT_ARTICLES_KEY, 0, 0);
            }
        } catch (Exception e) {
            log.error("记录文章访问次数失败, articleId: {}", articleId, e);
        }
    }

    /**
     * 判断是否是热门文章
     */
    private boolean isHotArticle(Long articleId) {
        try {
            ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
            Double score = zSetOps.score(HOT_ARTICLES_KEY, String.valueOf(articleId));
            return score != null && score >= HOT_ARTICLES_THRESHOLD;
        } catch (Exception e) {
            log.error("判断热门文章失败, articleId: {}", articleId, e);
            return false;
        }
    }

    /**
     * 从缓存获取文章详情
     */
    private Response getCachedArticleDetail(Long articleId) {
        try {
            String key = "article_detail:" + articleId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof Response) {
                return (Response) cached;
            }
        } catch (Exception e) {
            log.error("从缓存获取文章详情失败, articleId: {}", articleId, e);
        }
        return null;
    }

    /**
     * 缓存文章详情
     */
    private void cacheArticleDetail(Long articleId, Response response) {
        try {
            String key = "article_detail:" + articleId;
            redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(10));
        } catch (Exception e) {
            log.error("缓存文章详情失败, articleId: {}", articleId, e);
        }
    }
}

