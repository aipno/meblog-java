package cn.iswxl.meblog.search.event.subscriber;

import cn.iswxl.meblog.search.event.ReadArticleEvent;
import cn.iswxl.meblog.common.domain.mapper.ArticleMapper;
import cn.iswxl.meblog.common.domain.mapper.StatisticsArticlePVMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ReadArticleSubscriber implements ApplicationListener<ReadArticleEvent> {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private StatisticsArticlePVMapper articlePVMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Async("threadPoolTaskExecutor")
    public void onApplicationEvent(ReadArticleEvent event) {
        // 在这里处理收到的事件，可以是任何逻辑操作
        Long articleId = event.getArticleId();

        // 获取当前线程名称
        String threadName = Thread.currentThread().getName();

        log.info("==> threadName: {}", threadName);
        log.info("==> 文章阅读事件消费成功，articleId: {}", articleId);

        // 执行文章阅读量 +1 (使用Redis缓冲)
        String key = "article:read_count:" + articleId;
        redisTemplate.opsForValue().increment(key, 1);
        // 设置过期时间，防止key过多
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
        log.info("==> 文章阅读量 +1 操作成功（Redis缓冲），articleId: {}", articleId);

        // 当日文章 PV 访问量 +1
        LocalDate currDate = LocalDate.now();
        articlePVMapper.increasePVCount(currDate);
        log.info("==> 当日文章 PV 访问量 +1 操作成功，date: {}", currDate);
    }
}
