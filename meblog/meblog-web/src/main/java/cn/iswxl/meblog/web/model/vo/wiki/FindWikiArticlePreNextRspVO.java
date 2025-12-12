package cn.iswxl.meblog.web.model.vo.wiki;

import cn.iswxl.meblog.web.model.vo.archive.FindPreNextArticleRspVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindWikiArticlePreNextRspVO {
    /**
     * 上一篇文章
     */
    private FindPreNextArticleRspVO preArticle;
    /**
     * 下一篇文章
     */
    private FindPreNextArticleRspVO nextArticle;

}
