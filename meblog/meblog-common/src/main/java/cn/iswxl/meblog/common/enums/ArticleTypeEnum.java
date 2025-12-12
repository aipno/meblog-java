package cn.iswxl.meblog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ArticleTypeEnum {

    NORMAL(1, "普通文章"),
    WIKI(2, "收录于知识库");

    private Integer value;
    private String description;

}
