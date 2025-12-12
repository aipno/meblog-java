package cn.iswxl.meblog.web.model.vo.archive;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "文章归档 VO")
public class FindArchiveArticleRspVO {
    private Long id;
    private String cover;
    private String title;
    /**
     * 发布日期
     */
    private LocalDate createDate;

    /**
     * 发布的月份（此字段不需要展示在前端，主要用于按月份分组使用）
     */
    private YearMonth createMonth;
}

