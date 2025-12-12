package cn.iswxl.meblog.admin.model.vo.article;

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
@Schema(name = "更新文章发布状态 VO")
public class UpdateArticleIsPublishReqVO {

    @NotNull(message = "文章 ID 不能为空")
    private Long id;

    @NotNull(message = "文章发布状态不能为空")
    private Boolean isPublish;
}
