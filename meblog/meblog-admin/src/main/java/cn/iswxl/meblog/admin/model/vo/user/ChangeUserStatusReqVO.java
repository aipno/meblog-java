package cn.iswxl.meblog.admin.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "修改用户状态 VO")
public class ChangeUserStatusReqVO {

    private Long userId;

    private Integer status;
}
