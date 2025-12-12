package cn.iswxl.meblog.web.model.vo.wiki;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindWikiListRspVO {
    private Long id;
    private String cover;
    private String title;
    private String summary;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 第一篇文章 ID
     */
    private Long firstArticleId;
}
