package cn.iswxl.meblog.web.model.vo.wiki;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "查询上一篇下一篇请求 VO")
public class FindWikiArticlePreNextReqVO {

    @NotNull(message = "知识库 ID 不能为空")
    private Long id;

    @NotNull(message = "文章 ID 不能为空")
    private Long articleId;

}
