package cn.iswxl.meblog.admin.model.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "查询用户组权限列表入参 VO")
public class FindPermissionListReqVO {

    private Long roleId;
}
