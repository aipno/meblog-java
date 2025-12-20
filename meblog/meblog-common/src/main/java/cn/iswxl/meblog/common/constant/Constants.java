package cn.iswxl.meblog.common.constant;

import java.time.format.DateTimeFormatter;

public interface Constants {
    /**
     * 月-日 格式
     */
    DateTimeFormatter MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    /**
     * 年-月-日 小时-分钟-秒
     */
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 定义Redis缓存默认过期时间
     */
    int CACHE_TIMEOUT_HOUR = 2;

    /**
     * 定义unknown字串串的常量
     */
    String UNKNOWN = "unknown";

    /**
     * 定义MB的计算常量
     */
    int MB = 1024 * 1024;

    /**
     * 定义KB的计算常量
     */
    int KB = 1024;
}
