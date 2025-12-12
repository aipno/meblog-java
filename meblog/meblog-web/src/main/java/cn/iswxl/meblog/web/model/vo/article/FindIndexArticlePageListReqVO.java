package cn.iswxl.meblog.web.model.vo.article;

import cn.iswxl.meblog.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "首页查询文章分页 VO")
public class FindIndexArticlePageListReqVO extends BasePageQuery {
}

