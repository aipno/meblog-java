package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.ArticleDO;
import cn.iswxl.meblog.common.domain.dos.ArticlePublishCountDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public interface ArticleMapper extends BaseMapper<ArticleDO> {

    /**
     * 分页查询
     *
     * @param current   当前页码
     * @param size      每页展示的数据量
     * @param title     文章标题
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    default Page<ArticleDO> selectPageList(Long current, Long size, String title,
                                           LocalDate startDate, LocalDate endDate, Integer type, Integer IsPublish) {
        // 分页对象(查询第几页、每页多少数据)
        Page<ArticleDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<ArticleDO> wrapper = Wrappers.<ArticleDO>lambdaQuery()
                .like(StringUtils.isNotBlank(title), ArticleDO::getTitle, title) // like 模块查询
                .ge(Objects.nonNull(startDate), ArticleDO::getCreateTime, startDate) // 大于等于 startDate
                .le(Objects.nonNull(endDate), ArticleDO::getCreateTime, endDate)  // 小于等于 endDate
                .eq(Objects.nonNull(type), ArticleDO::getType, 1) // 文章类型
                .eq(Objects.nonNull(IsPublish), ArticleDO::getIsPublish, 1) // 文章是否发布
//                .orderByDesc(ArticleDO::getWeight) // 按权重倒序
                .orderByDesc(ArticleDO::getCreateTime); // 按创建时间倒叙

        return selectPage(page, wrapper);
    }

    /**
     * 根据文章 ID 批量分页查询
     */
    default Page<ArticleDO> selectPageListByArticleIds(Long current, Long size, List<Long> articleIds) {
        // 分页对象(查询第几页、每页多少数据)
        Page<ArticleDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<ArticleDO> wrapper = Wrappers.<ArticleDO>lambdaQuery()
                .in(ArticleDO::getId, articleIds) // 批量查询
                .orderByDesc(ArticleDO::getCreateTime); // 按创建时间倒叙

        return selectPage(page, wrapper);
    }

    /**
     * 根据文章 ID 批量分页查询（带过滤条件）
     */
    default Page<ArticleDO> selectPageListByArticleIds(Long current, Long size, List<Long> articleIds, Integer type, Integer isPublish) {
        // 分页对象(查询第几页、每页多少数据)
        Page<ArticleDO> page = new Page<>(current, size);

        // 构建查询条件
        LambdaQueryWrapper<ArticleDO> wrapper = Wrappers.<ArticleDO>lambdaQuery()
                .in(ArticleDO::getId, articleIds) // 批量查询
                .eq(Objects.nonNull(type), ArticleDO::getType, 1) // 文章类型
                .eq(Objects.nonNull(isPublish), ArticleDO::getIsPublish, 1) // 文章是否发布
                .orderByDesc(ArticleDO::getCreateTime); // 按创建时间倒叙

        return selectPage(page, wrapper);
    }

    /**
     * 查询上一篇文章
     *
     */
    default ArticleDO selectPreArticle(Long articleId) {
        return selectOne(Wrappers.<ArticleDO>lambdaQuery()
                .eq(ArticleDO::getIsPublish, 1)
                .eq(ArticleDO::getType,1)
                .orderByAsc(ArticleDO::getId) // 按文章 ID 升序排列
                .gt(ArticleDO::getId, articleId) // 查询比当前文章 ID 大的
                .last("limit 1")); // 第一条记录即为上一篇文章
    }

    /**
     * 查询下一篇文章
     *
     */
    default ArticleDO selectNextArticle(Long articleId) {
        return selectOne(Wrappers.<ArticleDO>lambdaQuery()
                .eq(ArticleDO::getIsPublish, 1)
                .eq(ArticleDO::getType,1)
                .orderByDesc(ArticleDO::getId) // 按文章 ID 倒序排列
                .lt(ArticleDO::getId, articleId) // 查询比当前文章 ID 小的
                .last("limit 1")); // 第一条记录即为下一篇文章
    }

    /**
     * 阅读量+1
     *
     */
    default void increaseReadNum(Long articleId) {
        // 执行 SQL : UPDATE t_article SET read_num = read_num + 1 WHERE (id = XX)
        update(null, Wrappers.<ArticleDO>lambdaUpdate()
                .setSql("read_num = read_num + 1")
                .eq(ArticleDO::getId, articleId));
    }

    /**
     * 批量增加阅读量
     *
     * @param articleId 文章ID
     * @param count     增加的数量
     */
    default void increaseReadNumByCount(Long articleId, Long count) {
        // 执行 SQL : UPDATE t_article SET read_num = read_num + ? WHERE (id = XX)
        update(null, Wrappers.<ArticleDO>lambdaUpdate()
                .setSql("read_num = read_num + " + count)
                .eq(ArticleDO::getId, articleId));
    }

    /**
     * 查询所有记录的阅读量
     *
     */
    default List<ArticleDO> selectAllReadNum() {
        // 设置仅查询 read_num 字段
        return selectList(Wrappers.<ArticleDO>lambdaQuery()
                .select(ArticleDO::getReadNum));
    }

    /**
     * 查询所有文章的阅读量总和
     *
     * @return 总阅读量
     */
    @Select("SELECT COALESCE(SUM(read_num), 0) FROM t_article")
    Long selectTotalReadNum();


    /**
     * 按日分组，并统计每日发布的文章数量
     */
    @Select("""
            SELECT DATE(create_time) AS date, COUNT(*) AS count
            FROM t_article
            WHERE create_time >= #{startDate} AND create_time < #{endDate}
            GROUP BY DATE(create_time)""")
    List<ArticlePublishCountDO> selectDateArticlePublishCount(LocalDate startDate, LocalDate endDate);


    /**
     * 批量更新文章
     */
    default void updateByIds(ArticleDO articleDO, List<Long> ids) {
        update(articleDO, Wrappers.<ArticleDO>lambdaUpdate()
                .in(ArticleDO::getId, ids)
        );
    }

    /**
     * 查询已发布的文章
     */
    default List<ArticleDO> selectPublished() {
        return selectList(Wrappers.<ArticleDO>lambdaQuery()
                        .eq(ArticleDO::getIsPublish, 1) // 查询已发布的， is_publish 值为 1
//                .orderByDesc(ArticleDO::getWeight) // 按权重降序
                        .orderByDesc(ArticleDO::getCreateTime) // 按发布时间降序
        );
    }
}
