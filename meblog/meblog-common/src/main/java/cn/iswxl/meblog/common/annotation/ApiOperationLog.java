package cn.iswxl.meblog.common.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
// 自定义注解
public @interface ApiOperationLog {
    /**
     * API 功能秒睡
     *
     * @return
     */
    String description() default "";
}
