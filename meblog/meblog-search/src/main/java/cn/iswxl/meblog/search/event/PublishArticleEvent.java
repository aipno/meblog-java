package cn.iswxl.meblog.search.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PublishArticleEvent extends ApplicationEvent {

    /**
     * 文章 ID
     */
    private Long articleId;

    public PublishArticleEvent(Object source, Long articleId) {
        super(source);
        this.articleId = articleId;
    }
}
