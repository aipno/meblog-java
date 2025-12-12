package cn.iswxl.meblog.admin.schedule;

import cn.iswxl.meblog.common.domain.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ArticleReadCountSyncTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 定时同步文章阅读量到数据库
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5分钟
    public void syncArticleReadCount() {
        try {
            log.info("开始同步文章阅读量到数据库...");

            // 查找所有文章阅读量的key
            Set<String> keys = redisTemplate.keys("article:read_count:*");

            if (CollectionUtils.isEmpty(keys)) {
                log.info("没有需要同步的文章阅读量数据");
                return;
            }

            int syncCount = 0;
            for (String key : keys) {
                try {
                    // 提取文章ID
                    String articleIdStr = key.replace("article:read_count:", "");
                    Long articleId = Long.parseLong(articleIdStr);

                    // 获取Redis中的阅读量
                    Object value = redisTemplate.opsForValue().get(key);
                    if (value != null) {
                        Long readCount = Long.parseLong(value.toString());

                        // 更新数据库中的阅读量
                        articleMapper.increaseReadNumByCount(articleId, readCount);

                        // 删除已同步的key
                        redisTemplate.delete(key);

                        syncCount++;
                        log.info("同步文章阅读量成功，articleId: {}, readCount: {}", articleId, readCount);
                    }
                } catch (Exception e) {
                    log.error("同步单个文章阅读量失败，key: {}", key, e);
                }
            }

            log.info("同步文章阅读量完成，共同步 {} 条数据", syncCount);
        } catch (Exception e) {
            log.error("同步文章阅读量到数据库失败", e);
        }
    }
}
