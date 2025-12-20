package cn.iswxl.meblog.admin.model.vo.article;

import cn.iswxl.meblog.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "查询文章分页数据入参 VO")
public class FindArticlePageListReqVO extends BasePageQuery {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 发布的起始日期
     */
    private LocalDate startDate;

    /**
     * 发布的结束日期
     */
    private LocalDate endDate;

    /**
     * 文章类型
     */
    private Integer type;

    /**
     * 是否发布
     */
    private Integer isPublish;

}

