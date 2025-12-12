package cn.iswxl.meblog.admin.model.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "查询用户组所有用户出参 VO")
public class FindRoleUserInfoListRspVO {

    private Long userId;

    private String username;

    private String nickname;

}
