package cn.iswxl.meblog.admin.model.vo.tag;

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
@Schema(name = "删除标签 VO")
public class DeleteTagReqVO {

    @NotNull(message = "标签 ID 不能为空")
    private Long id;

}

