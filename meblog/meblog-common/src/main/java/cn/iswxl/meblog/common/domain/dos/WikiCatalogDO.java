package cn.iswxl.meblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_wiki_catalog")
public class WikiCatalogDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long  wikiId;

    private Long articleId;

    private String title;

    private Integer level;

    private Long parentId;

    private Integer sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean isDeleted;
}
