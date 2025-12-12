package cn.iswxl.meblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplateEnum {
    // 验证码邮件
    VERIFICATION_CODE_EMAIL_HTML("<html><body>用户你好，你的验证码是:<h1>%s</h1>请在五分钟内完成注册</body></html>","登录验证"),

    // 用户被封禁邮件通知
    USER_BANNED_EMAIL("用户你好，你已经被管理员封禁，封禁原因:%s", "封禁通知");

    private final String template;
    private final String subject;

}
