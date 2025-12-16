package cn.iswxl.meblog.common.enums;

import cn.iswxl.meblog.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("10000", "出错啦，后台小哥正在努力修复中..."),

    PARAM_NOT_VALID("10001", "参数错误"),
    // ----------- 业务异常状态码 -----------
    LOGIN_FAIL("20000", "登录失败"),

    USERNAME_OR_PWD_ERROR("20001", "用户名或密码错误"),

    UNAUTHORIZED("20002", "无访问权限，请先登录！"),

    USERNAME_NOT_FOUND("20003", "该用户不存在"),

    FORBIDDEN("20004", "您的权限不足以进行操作！"),

    CATEGORY_NAME_IS_EXISTED("20005", "该分类已存在，请勿重复添加！"),

    TAG_CANT_DUPLICATE("20006", "请勿添加表中已存在的标签！"),

    TAG_NOT_EXISTED("20007", "该标签不存在！"),

    FILE_UPLOAD_FAILED("20008", "文件上传失败！"),

    CATEGORY_NOT_EXISTED("20009", "提交的分类不存在！"),

    ARTICLE_NOT_FOUND("20010", "该文章不存在！"),

    CATEGORY_CAN_NOT_DELETE("20011", "该分类下包含文章，请先删除对应文章，才能删除！"),

    TAG_CAN_NOT_DELETE("20012", "该标签下包含文章，请先删除对应文章，才能删除！"),

    WIKI_NOT_FOUND("20013", "该知识库不存在！"),

    WIKI_CAN_NOT_DELETE("20014", "该知识库下包含文章，请先删除对应文章，才能删除！"),

    USER_STATUS_IS_FALSE("20015", "该用户无法登录！"),
    
    OLD_PASSWORD_REQUIRED("20016", "旧密码不能为空"),
    
    OLD_PASSWORD_ERROR("20017", "旧密码错误"),
    
    PASSWORD_UPDATE_FAILED("20018", "密码更新失败"),

    TOKEN_NOT_VALID("20019", "Token 无效"),

    TOKEN_NOT_EXISTED("20020", "Token 不存在"),

    USERNAME_OR_PWD_REQUIRED("20021", "用户名或密码不能为空"),
    ;

    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

}
