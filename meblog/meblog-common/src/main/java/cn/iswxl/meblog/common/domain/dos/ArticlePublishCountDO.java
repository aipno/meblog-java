package cn.iswxl.meblog.common.domain.dos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticlePublishCountDO {
    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 文章发布数量
     */
    private Long count;
}
