package cn.iswxl.meblog.admin.model.vo.wiki;

import cn.iswxl.meblog.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "查询知识库分页数据入参 VO")
public class FindWikiPageListReqVO  extends BasePageQuery {

    /**
     * 知识库标题
     */
    private String title;

    /**
     * 发布的起始时间
     */
    private LocalDate startDate;

    /**
     * 发布的结束时间
     */
    private LocalDate endDate;
}
