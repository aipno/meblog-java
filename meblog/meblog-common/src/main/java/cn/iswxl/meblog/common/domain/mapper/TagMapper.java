package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.TagDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public interface TagMapper extends BaseMapper<TagDO> {

    /**
     * 分页查询
     *
     */
    default Page<TagDO> selectPageList(long current, long size, String name, LocalDate startDate, LocalDate endDate) {
        // 分页对象
        Page<TagDO> page = new Page<>(current, size);
        // 构建查询条件
        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .like(Objects.nonNull(name), TagDO::getName, name) // like 模块查询
                .ge(Objects.nonNull(startDate), TagDO::getCreateTime, startDate) // 大于等于开始时间
                .le(Objects.nonNull(endDate), TagDO::getCreateTime, endDate) // 小于等于结束时间
                .orderByDesc(TagDO::getCreateTime); // order by create_time desr

        return selectPage(page, wrapper);
    }


    /**
     * 根据关键字模糊查询
     *
     */
    default List<TagDO> selectByKey(String key) {
        LambdaQueryWrapper<TagDO> wrapper = new LambdaQueryWrapper<>();

        // 构造模糊查询条件
        wrapper
                .like(TagDO::getName, key)
                .orderByDesc(TagDO::getCreateTime);

        return selectList(wrapper);
    }

    /**
     * 根据标签 ID 批量查询
     *
     */
    default List<TagDO> selectByIds(List<Long> tagIds) {
        return selectList(Wrappers.<TagDO>lambdaQuery()
                .in(TagDO::getId, tagIds));
    }

    /**
     * 根据文章 ID 查询
     */
    default List<TagDO> selectByArticleId(Long articleId) {
        return selectList(Wrappers.<TagDO>lambdaQuery()
                .eq(TagDO::getId, articleId));
    }

    /**
     * 根据标签 ID 更新标签名称
     */
    default void updateTagNameById(Long tagId, String tagName) {
        update(null, Wrappers.<TagDO>lambdaUpdate()
                .eq(TagDO::getId, tagId)
                .set(TagDO::getName, tagName)
        );
    }

}

