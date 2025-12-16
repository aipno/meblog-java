package cn.iswxl.meblog.admin.service.impl;

import cn.iswxl.meblog.admin.model.vo.tag.*;
import cn.iswxl.meblog.admin.service.AdminTagService;
import cn.iswxl.meblog.common.domain.dos.ArticleTagRelDO;
import cn.iswxl.meblog.common.domain.dos.TagDO;
import cn.iswxl.meblog.common.domain.mapper.ArticleTagRelMapper;
import cn.iswxl.meblog.common.domain.mapper.TagMapper;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.common.model.vo.SelectRspVO;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminTagServiceImpl extends ServiceImpl<TagMapper, TagDO> implements AdminTagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagRelMapper articleTagRelMapper;

    @Override
    public Response addTags(AddTagReqVO addTagReqVO) {
        // 获取标签名称列表
        List<String> tagNames = addTagReqVO.getTags();

        // 查询已存在的标签
        List<TagDO> existingTags = tagMapper.selectList(Wrappers.<TagDO>lambdaQuery()
                .in(TagDO::getName, tagNames));

        // 提取已存在的标签名称
        List<String> existingTagNames = existingTags.stream()
                .map(TagDO::getName)
                .collect(Collectors.toList());

        // 过滤出需要新增的标签名称
        List<String> newTagNames = tagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .collect(Collectors.toList());

        // 如果有重复的标签，记录日志
        if (!existingTagNames.isEmpty()) {
            log.warn("以下标签已存在: {}", String.join(", ", existingTagNames));
        }

        // 如果没有需要新增的标签，直接返回
        if (newTagNames.isEmpty()) {
            return Response.success();
        }

        // 构造需要新增的标签DO列表
        List<TagDO> newTagDOS = newTagNames.stream()
                .map(tagName -> TagDO.builder()
                        .name(tagName.trim()) // 去掉前后空格
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        // 批量插入新标签
        try {
            if (!newTagDOS.isEmpty()) {
                saveBatch(newTagDOS);
            }
        } catch (DuplicateKeyException e) {
            log.error("标签插入失败，存在重复标签: {}", e.getMessage());
            throw new BizException(ResponseCodeEnum.TAG_CANT_DUPLICATE);
        } catch (Exception e) {
            log.error("标签插入失败: {}", e.getMessage(), e);
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR);
        }

        return Response.success();
    }

    @Override
    public PageResponse findTagPageList(FindTagPageListReqVO findTagPageListReqVO) {
        // 分页参数、条件参数
        Long current = findTagPageListReqVO.getCurrent();
        Long size = findTagPageListReqVO.getSize();
        String name = findTagPageListReqVO.getName();
        LocalDate startDate = findTagPageListReqVO.getStartDate();
        LocalDate endDate = findTagPageListReqVO.getEndDate();

        // 分页查询
        Page<TagDO> page = tagMapper.selectPageList(current, size, name, startDate, endDate);

        List<TagDO> records = page.getRecords();

        // do -> vo
        List<FindTagPageListRspVO> vos = null;
        if (!CollectionUtils.isEmpty(records)) {
            vos = records.stream()
                    .map(tagDO -> FindTagPageListRspVO.builder()
                            .id(tagDO.getId())
                            .name(tagDO.getName())
                            .createTime(tagDO.getCreateTime())
                            .articlesTotal(tagDO.getArticlesTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return PageResponse.success(page, vos);
    }

    /**
     * 删除标签
     *
     * @param deleteTagReqVO
     * @return
     */
    @Override
    public Response deleteTag(DeleteTagReqVO deleteTagReqVO) {
        // 标签 ID
        Long tagId = deleteTagReqVO.getId();

        // 校验该标签下是否有关联的文章，若有，则不允许删除，提示用户需要先删除标签下的文章
        ArticleTagRelDO articleTagRelDO = articleTagRelMapper.selectOneByTagId(tagId);

        if (Objects.nonNull(articleTagRelDO)) {
            log.warn("==> 此标签下包含文章，无法删除，tagId: {}", tagId);
            throw new BizException(ResponseCodeEnum.TAG_CAN_NOT_DELETE);
        }

        // 根据标签 ID 删除
        int count = tagMapper.deleteById(tagId);

        return count == 1 ? Response.success() : Response.fail(ResponseCodeEnum.TAG_NOT_EXISTED);
    }

    @Override
    public Response searchTag(SearchTagReqVO searchTagReqVO) {
        String key = searchTagReqVO.getKey();

        // 执行模糊查询
        List<TagDO> tagDOS = tagMapper.selectByKey(key);

        // do -> vo
        List<SelectRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    @Override
    public Response findTagSelectList() {
        // 查询所有标签, Wrappers.emptyWrapper() 表示查询条件为空
        List<TagDO> tagDOS = tagMapper.selectList(Wrappers.emptyWrapper());

        // DO 转 VO
        List<SelectRspVO> vos = null;
        if (!CollectionUtils.isEmpty(tagDOS)) {
            vos = tagDOS.stream()
                    .map(tagDO -> SelectRspVO.builder()
                            .label(tagDO.getName())
                            .value(tagDO.getId())
                            .build())
                    .collect(Collectors.toList());
        }

        return Response.success(vos);
    }

    @Override
    public Response updateTag(UpdateTagReqVO updateTagReqVO) {
        Long tagId = updateTagReqVO.getId();
        String tagName = updateTagReqVO.getName();
        tagMapper.updateTagNameById(tagId,tagName);
        return Response.success();
    }
}

