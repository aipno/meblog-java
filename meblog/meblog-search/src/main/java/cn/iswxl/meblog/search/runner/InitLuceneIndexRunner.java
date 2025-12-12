package cn.iswxl.meblog.search.runner;

import cn.iswxl.meblog.common.domain.dos.ArticleContentDO;
import cn.iswxl.meblog.common.domain.dos.ArticleDO;
import cn.iswxl.meblog.common.domain.mapper.ArticleContentMapper;
import cn.iswxl.meblog.common.domain.mapper.ArticleMapper;
import cn.iswxl.meblog.search.LuceneHelper;
import cn.iswxl.meblog.search.config.LuceneProperties;
import cn.iswxl.meblog.search.index.ArticleIndex;
import cn.iswxl.meblog.common.constant.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@Slf4j
public class InitLuceneIndexRunner implements CommandLineRunner {

    @Autowired
    private LuceneProperties luceneProperties;
    @Autowired
    private LuceneHelper luceneHelper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("==> 开始初始化 Lucene 索引...");

        // 查询所有文章
        List<ArticleDO> articleDOS = articleMapper.selectList(Wrappers.emptyWrapper());

        // 未发布文章，则不创建 lucene 索引
        if (articleDOS.isEmpty()) {
            log.info("==> 未发布任何文章，暂不创建 Lucene 索引...");
            return;
        }

        // 若配置文件中未配置索引存放目录，日志提示错误信息
        if (StringUtils.isBlank(luceneProperties.getIndexDir())) {
            log.error("==> 未指定 Lucene 索引存放位置，需在 application.yml 文件中添加路径配置...");
            return;
        }

        // 文章索引存放目录， 如 /app/weblog/lucene-index/article
        String articleIndexDir = luceneProperties.getIndexDir() + File.separator + ArticleIndex.NAME;

        List<Document> documents = Lists.newArrayList();
        articleDOS.forEach(articleDO -> {
            Long articleId = articleDO.getId();

            // 查询文章正文
            ArticleContentDO articleContentDO = articleContentMapper.selectByArticleId(articleId);
            // 构建文档
            Document document = new Document();
            // 设置文档字段 Field
            document.add(new TextField(ArticleIndex.COLUMN_ID, String.valueOf(articleId), Field.Store.YES));
            document.add(new TextField(ArticleIndex.COLUMN_TITLE, articleDO.getTitle(), Field.Store.YES));
            document.add(new TextField(ArticleIndex.COLUMN_SUMMARY, articleDO.getSummary(), Field.Store.YES));
            document.add(new TextField(ArticleIndex.COLUMN_CONTENT, articleContentDO.getContent(), Field.Store.YES));
            document.add(new TextField(ArticleIndex.COLUMN_CREATE_TIME, Constants.DATE_TIME_FORMATTER.format(articleDO.getCreateTime()), Field.Store.YES));
            documents.add(document);
        });

        // 创建索引
        luceneHelper.createIndex(articleIndexDir, documents);

        log.info("==> 结束初始化 Lucene 索引...");
    }
}
