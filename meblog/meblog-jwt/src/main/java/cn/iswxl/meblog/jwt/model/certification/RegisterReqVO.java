package cn.iswxl.meblog.jwt.model.certification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证用户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "注册请求 VO")
public class RegisterReqVO {

    private String username;

    private String password;

    private String confirmPassword;

    private String email ;

    private String code;
}
