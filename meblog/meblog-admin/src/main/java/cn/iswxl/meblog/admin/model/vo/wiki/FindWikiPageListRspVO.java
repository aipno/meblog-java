package cn.iswxl.meblog.admin.model.vo.wiki;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindWikiPageListRspVO {

    /**
     * 知识库 ID
     */
    private Long id;

    /**
     * 知识库标题
     */
    private String title;

    /**
     * 知识库封面
     */
    private String cover;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 是否发布
     */
    private Boolean isPublish;
}
