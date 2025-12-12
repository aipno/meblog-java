package cn.iswxl.meblog.common.model.vo.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 邮箱信息
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailRspVO {


    /**
     * 发送邮箱列表
     * */
    @NotEmpty
    private List<String> tos;

    /**
     * 主题
     * */
    @NotBlank
    private String subject;

    /**
     * 内容
     * */
    @NotBlank
    private String content;
}

