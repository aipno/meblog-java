package cn.iswxl.meblog.admin.model.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "标签模糊查询 VO")
public class SearchTagReqVO {

    @NotBlank(message = "标签查询关键词不能为空")
    private String key;

}

