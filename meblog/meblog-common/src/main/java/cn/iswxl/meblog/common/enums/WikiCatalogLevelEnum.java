package cn.iswxl.meblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WikiCatalogLevelEnum {
    // 一级目录
    ONE(1),
    // 二级目录
    TWO(2);

    private Integer value;
}
