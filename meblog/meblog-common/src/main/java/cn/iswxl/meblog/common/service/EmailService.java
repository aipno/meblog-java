package cn.iswxl.meblog.common.service;

import cn.iswxl.meblog.common.model.vo.email.SendEmailRspVO;
import org.springframework.stereotype.Service;

/**
 * 邮箱服务接口
 * */

@Service
public interface EmailService {

    /**
     * 发送邮件
     * */
    void send(SendEmailRspVO sendEmailRspVO);
}
