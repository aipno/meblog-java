package cn.iswxl.meblog.web.controller;

import cn.iswxl.meblog.common.annotation.ApiOperationLog;
import cn.iswxl.meblog.common.model.vo.email.SendEmailReqVO;
import cn.iswxl.meblog.common.utils.Response;
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
@RequestMapping("/email")
@Tag(name = "邮件")
public class EmailController {

    @Autowired
    private AuthService authService;

//    @PostMapping("/send")
//    @Operation(description = "发送邮件")
//    public Response sendEmail(@RequestBody @Validated SendEmailRspVO sendEmailRspVO) {
//        emailService.send(sendEmailRspVO);
//        return Response.success("发送成功");
//    }

//    @PostMapping("/code")
//    @Operation(description = "发送注册验证码")
//    @ApiOperationLog(description = "发送注册验证码")
//    public Response sendEmailTest(@RequestBody @Validated SendEmailReqVO sendEmailReqVO) {
//        authService.sendMailCode(sendEmailReqVO.getEmail());
//        System.out.println(sendEmailReqVO.getEmail());
//        return Response.success("发送成功");
//    }
}
