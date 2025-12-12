package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.ArticlePermissionDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

public interface ArticlePermissionMapper extends BaseMapper<ArticlePermissionDO> {

    /**
     * 新增文章权限
     * @param articleId
     */
    default void addPermission(Long articleId) {
        ArticlePermissionDO articlePermissionDO = ArticlePermissionDO.builder()
                .articleId(articleId)
                .isPermission(true)
                .build();
        insert(articlePermissionDO);
    }

    /**
     * 更新文章权限
     * @param articleId
     * @param isPermission
     */
    default void updatePermission(Long articleId, Boolean isPermission) {
        // 创建更新引擎并设置更新条件
        LambdaUpdateWrapper<ArticlePermissionDO> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(ArticlePermissionDO::getArticleId, articleId)
                      .set(ArticlePermissionDO::getIsPermission, isPermission);

        // 执行更新并返回
        update(null, updateWrapper);
    }


    /**
     * 根据文章ID查询文章权限
     * @param articleId
     * @return
     */
    default Boolean isPermission(Long articleId) {
        // 创建查询引擎
        LambdaQueryWrapper<ArticlePermissionDO> queryWrapper = Wrappers.lambdaQuery();
        // 创建查询条件
        queryWrapper.eq(ArticlePermissionDO::getArticleId, articleId)
                .select(ArticlePermissionDO::getIsPermission);
        // 执行查询并返回
        return selectOne(queryWrapper).getIsPermission();
    }

    /**
     * 删除文章权限
     * @param articleId
     */
    default void deletePermission(Long articleId) {
        delete(Wrappers.<ArticlePermissionDO>lambdaQuery()
                .eq(ArticlePermissionDO::getArticleId, articleId));
    }
}
