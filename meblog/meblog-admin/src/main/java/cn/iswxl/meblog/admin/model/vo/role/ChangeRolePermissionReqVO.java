package cn.iswxl.meblog.admin.model.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "更新角色权限入参 VO")
public class ChangeRolePermissionReqVO {

    private Long roleId;

    private Set<Long> permissionIds;
}
