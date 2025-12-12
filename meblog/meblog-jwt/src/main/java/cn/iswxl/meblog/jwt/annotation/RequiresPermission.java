package cn.iswxl.meblog.jwt.annotation;

import cn.iswxl.meblog.common.enums.LogicalEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    /**
     * 需要的权限码数组
     */
    String[] value();

    /**
     * 验证逻辑：AND-需要所有权限，OR-只需其中一个权限
     */
    LogicalEnum logical() default LogicalEnum.AND;

    /**
     * 权限验证失败时的错误消息
     */
    String message() default "没有访问权限";
}

