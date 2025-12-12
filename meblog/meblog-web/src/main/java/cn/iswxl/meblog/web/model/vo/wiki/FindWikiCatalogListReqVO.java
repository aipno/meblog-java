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
@Schema(name = "知识库目录列表入参 VO")
public class FindWikiCatalogListReqVO {

    @NotNull(message = "知识库 ID 不能为空")
    private Long id;

}
