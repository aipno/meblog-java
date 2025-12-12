package cn.iswxl.meblog.web.model.vo.archive;

import cn.iswxl.meblog.common.model.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Schema(name = "文章归档分页 VO")
public class FindArchiveArticlePageListReqVO extends BasePageQuery {
}

