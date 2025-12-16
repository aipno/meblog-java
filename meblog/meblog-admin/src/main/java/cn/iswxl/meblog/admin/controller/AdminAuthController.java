package cn.iswxl.meblog.admin.controller;

import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.utils.Response;
import cn.iswxl.meblog.jwt.model.certification.LoginReqVO;
import cn.iswxl.meblog.jwt.model.certification.LoginRspVO;
import cn.iswxl.meblog.jwt.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Tag(name = "认证")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(description = "登陆")
    @ApiOperationLog(description = "登陆")
    public Response Login(@RequestBody @Validated LoginReqVO loginReqVO) {
        LoginRspVO token = authService.login(loginReqVO);
        return Response.success(token);
    }

    @PostMapping("/logout")
    @Operation(description = "登出")
    @ApiOperationLog(description = "登出")
    public Response logout() {
        authService.logout();
        return Response.success();
    }
}
