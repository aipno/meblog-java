package cn.iswxl.meblog.web.model.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "分类列表入参 VO")
public class FindCategoryListReqVO {

    /**
     * 展示数量
     */
    private Long size;

}

