package cn.iswxl.meblog.jwt.model.certification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "登录返回 VO")
public class LoginRspVO {
    /**
     * Token 值
     */
    private String token;
}
