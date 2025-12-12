package cn.iswxl.meblog.common.model.vo.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailReqVO {

    @Email
    String email;
}
