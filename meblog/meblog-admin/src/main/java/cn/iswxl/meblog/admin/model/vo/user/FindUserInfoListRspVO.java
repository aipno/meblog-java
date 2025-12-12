package cn.iswxl.meblog.admin.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserInfoListRspVO {

    private Long id;

    private String username;

    private String nickname;

    private String role;

    private Integer status;

    private LocalDateTime createTime;
}
