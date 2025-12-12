package cn.iswxl.meblog.jwt.model.certification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "登录请求 VO")
public class LoginReqVO {

    @NotNull
    private String username;

    @NotNull
    private String password;
}
