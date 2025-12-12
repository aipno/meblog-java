package cn.iswxl.meblog.jwt.annotation;

import cn.iswxl.meblog.common.enums.LogicalEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRoles {
    String[] value();

    LogicalEnum logical() default LogicalEnum.AND;

    String message() default "没有访问权限";
}
