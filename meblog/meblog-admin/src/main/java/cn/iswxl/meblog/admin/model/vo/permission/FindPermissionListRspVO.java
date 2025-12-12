package cn.iswxl.meblog.admin.model.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "查询用户组权限列表出参 VO")
public class FindPermissionListRspVO {

    private String permissionParentName;

    private List<PermissionListRspVO> permissionList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PermissionListRspVO {

        private Long permissionId;

        private String permissionName;

        private Boolean status;

        private String permissionDesc;

    }
}
