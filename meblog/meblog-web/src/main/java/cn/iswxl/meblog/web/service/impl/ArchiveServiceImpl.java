package cn.iswxl.meblog.web.service.impl;

import cn.iswxl.meblog.common.domain.dos.*;
import cn.iswxl.meblog.common.domain.mapper.*;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.web.convert.ArticleConvert;
import cn.iswxl.meblog.web.model.vo.archive.*;
import cn.iswxl.meblog.web.service.ArchiveService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArchiveServiceImpl implements ArchiveService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ImageMapper imageMapper;

    /**
     * 获取文章归档分页数据
     *
     * @param findArchiveArticlePageListReqVO
     * @return
     */
    @Override
    public Response findArchivePageList(FindArchiveArticlePageListReqVO findArchiveArticlePageListReqVO) {
        Long current = findArchiveArticlePageListReqVO.getCurrent();
        Long size = findArchiveArticlePageListReqVO.getSize();

        // 分页查询
        IPage<ArticleDO> page = articleMapper.selectPageList(current, size, null, null, null,1,1);
        List<ArticleDO> articleDOS = page.getRecords();

        List<FindArchiveArticlePageListRspVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(articleDOS)) {
            // DO 转 VO
            List<FindArchiveArticleRspVO> archiveArticleRspVOS =  articleDOS.stream()
                    .map(ArticleConvert.INSTANCE::convertDO2ArchiveArticleVO)
                    .toList();

            // 设置文章封面图片
            archiveArticleRspVOS.forEach(vo -> {
                // 通过文章ID找到对应的文章DO对象
                Optional<ArticleDO> articleOptional = articleDOS.stream()
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

            // 按创建的月份进行分组
            Map<YearMonth, List<FindArchiveArticleRspVO>> map = archiveArticleRspVOS.stream().collect(Collectors.groupingBy(FindArchiveArticleRspVO::getCreateMonth));
            // 使用 TreeMap 按月份倒序排列
            Map<YearMonth, List<FindArchiveArticleRspVO>> sortedMap = new TreeMap<>(Collections.reverseOrder());
            sortedMap.putAll(map);

            // 遍历排序后的 Map，将其转换为归档 VO
            sortedMap.forEach((k, v) -> vos.add(FindArchiveArticlePageListRspVO.builder().month(k).articles(v).build()));
        }

        return PageResponse.success(page, vos);
    }

}

