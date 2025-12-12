package cn.iswxl.meblog.web.service.impl;

import cn.iswxl.meblog.common.domain.dos.ArticleDO;
import cn.iswxl.meblog.common.domain.dos.ArticleTagRelDO;
import cn.iswxl.meblog.common.domain.dos.ImageDO;
import cn.iswxl.meblog.common.domain.dos.TagDO;
import cn.iswxl.meblog.common.domain.mapper.ArticleMapper;
import cn.iswxl.meblog.common.domain.mapper.ArticleTagRelMapper;
import cn.iswxl.meblog.common.domain.mapper.ImageMapper;
import cn.iswxl.meblog.common.domain.mapper.TagMapper;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.web.convert.ArticleConvert;
import cn.iswxl.meblog.web.model.vo.tag.FindTagArticlePageListReqVO;
import cn.iswxl.meblog.web.model.vo.tag.FindTagArticlePageListRspVO;
import cn.iswxl.meblog.web.model.vo.tag.FindTagListRspVO;
import cn.iswxl.meblog.web.service.TagService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ImageMapper imageMapper;

    /**
     * 获取标签列表
     *
     * @return
     */
    @Override
    public Response findTagList() {
        // 查询所有标签
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        // DO 转 VO
        List<FindTagListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> FindTagListRspVO.builder()
                            .id(tagDO.getId())
                            .name(tagDO.getName())
                            .articlesTotal(tagDO.getArticlesTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    /**
     * 获取标签下文章分页列表
     *
     * @param findTagArticlePageListReqVO
     * @return
     */
    @Override
    public Response findTagPageList(FindTagArticlePageListReqVO findTagArticlePageListReqVO) {
        Long current = findTagArticlePageListReqVO.getCurrent();
        Long size = findTagArticlePageListReqVO.getSize();
        // 标签 ID
        Long tagId = findTagArticlePageListReqVO.getId();

        // 判断该标签是否存在
        TagDO tagDO = tagMapper.selectById(tagId);
        if (Objects.isNull(tagDO)) {
            log.warn("==> 该标签不存在, tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_NOT_EXISTED);
        }

        // 先查询该标签下所有关联的文章 ID
        List<ArticleTagRelDO> articleTagRelDOS = articleTagRelMapper.selectByTagId(tagId);

        // 若该标签下未发布任何文章
        if (CollectionUtils.isEmpty(articleTagRelDOS)) {
            log.info("==> 该标签下还未发布任何文章, tagId: {}", tagId);
            return PageResponse.success(null, null);
        }

        // 提取所有文章 ID
        List<Long> articleIds = articleTagRelDOS.stream().map(ArticleTagRelDO::getArticleId).collect(Collectors.toList());

        // 根据文章 ID 集合查询文章分页数据（在数据库层面过滤掉未发布和非普通类型的文章）
        Page<ArticleDO> page = articleMapper.selectPageListByArticleIds(current, size, articleIds, 1, 1); // type=1表示普通文章，isPublish=1表示已发布
        List<ArticleDO> articleDOS = page.getRecords();

        // DO 转 VO
        List<FindTagArticlePageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(articleDOS)) {
            vos = articleDOS.stream()
                    .map(ArticleConvert.INSTANCE::convertDO2TagArticleVO)
                    .collect(Collectors.toList());
        }

        // 设置文章封面图片
        List<FindTagArticlePageListRspVO> finalVos = vos;
        List<ArticleDO> finalArticleDOS = articleDOS;
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

        return PageResponse.success(page, finalVos);
    }
}

