package cn.iswxl.meblog.search;

import cn.iswxl.meblog.search.config.LuceneProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class LuceneHelper {

    @Autowired
    private LuceneProperties properties;

    // 使用ConcurrentHashMap存储不同索引的IndexWriter实例，实现单例模式
    private final ConcurrentHashMap<String, IndexWriter> indexWriterMap = new ConcurrentHashMap<>();
    
    // 使用ConcurrentHashMap存储不同索引的SearcherManager实例
    private final ConcurrentHashMap<String, SearcherManager> searcherManagerMap = new ConcurrentHashMap<>();

    /**
     * 初始化方法，确保组件启动时正确初始化
     */
    @PostConstruct
    public void init() {
        log.info("LuceneHelper initialized.");
    }

    /**
     * 销毁方法，关闭所有资源
     */
    @PreDestroy
    public void destroy() {
        // 关闭所有IndexWriter
        indexWriterMap.values().forEach(writer -> {
            try {
                if (writer != null && writer.isOpen()) {
                    writer.close();
                }
            } catch (IOException e) {
                log.error("Error closing IndexWriter: ", e);
            }
        });
        indexWriterMap.clear();

        // 关闭所有SearcherManager
        searcherManagerMap.values().forEach(searcherManager -> {
            try {
                if (searcherManager != null) {
                    searcherManager.close();
                }
            } catch (IOException e) {
                log.error("Error closing SearcherManager: ", e);
            }
        });
        searcherManagerMap.clear();
    }

    /**
     * 获取指定索引的IndexWriter实例（单例模式）
     * @param indexDir 索引目录路径
     * @return IndexWriter实例
     */
    private IndexWriter getIndexWriter(String indexDir) throws IOException {
        return indexWriterMap.computeIfAbsent(indexDir, dir -> {
            try {
                Directory directory = FSDirectory.open(Paths.get(dir));
                Analyzer analyzer = new SmartChineseAnalyzer();
                IndexWriterConfig config = new IndexWriterConfig(analyzer);
                // 启用近实时搜索支持
                config.setUseCompoundFile(false);
                return new IndexWriter(directory, config);
            } catch (IOException e) {
                log.error("Failed to create IndexWriter for directory: {}", dir, e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 获取指定索引的SearcherManager实例
     * @param indexDir 索引目录路径
     * @return SearcherManager实例
     */
    private SearcherManager getSearcherManager(String indexDir) throws IOException {
        return searcherManagerMap.computeIfAbsent(indexDir, dir -> {
            try {
                Directory directory = FSDirectory.open(Paths.get(dir));
                // 使用近实时SearcherManager
                return new SearcherManager(directory, null);
            } catch (IOException e) {
                log.error("Failed to create SearcherManager for directory: {}", dir, e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 创建索引
     * @param indexDir 索引名称
     * @param documents 文档
     */
    public void createIndex(String indexDir, List<Document> documents) {
        try {
            File dir = new File(indexDir);

            // 判断索引目录是否存在
            if (dir.exists()) {
                // 删除目录中的内容
                FileUtils.cleanDirectory(dir);
            } else {
                // 若不存在，则创建目录
                FileUtils.forceMkdir(dir);
            }

            // 获取IndexWriter实例
            IndexWriter writer = getIndexWriter(indexDir);

            // 清空现有文档
            writer.deleteAll();

            // 添加文档
            documents.forEach(document -> {
                try {
                    writer.addDocument(document);
                } catch (IOException e) {
                    log.error("添加 Lucene 文档错误: ", e);
                }
            });

            // 提交
            writer.commit();
            
            // 更新对应的SearcherManager
            SearcherManager searcherManager = searcherManagerMap.get(indexDir);
            if (searcherManager != null) {
                searcherManager.maybeRefresh();
            }
        } catch (Exception e) {
            log.error("创建 Lucene 索引失败: ", e);
        }
    }

    /**
     * 关键词搜索, 查询总数据量
     * @param index 索引名称
     * @param word 查询关键词
     * @param columns 需要搜索的字段
     * @return
     */
    public long searchTotal(String index, String word, String[] columns) {
        String indexDir = properties.getIndexDir() + File.separator + index;
        IndexSearcher searcher = null;
        
        try {
            // 获取SearcherManager并获取最新的IndexSearcher
            SearcherManager searcherManager = getSearcherManager(indexDir);
            searcher = searcherManager.acquire();
            
            // 构造查询条件（简化示例，实际可根据columns构造更复杂的查询）
            Query query = new TermQuery(new Term(columns[0], word));
            
            // 执行查询并返回总数
            return searcher.count(query);
        } catch (Exception e) {
            log.error("查询 Lucene 错误: ", e);
            return 0;
        } finally {
            // 释放IndexSearcher
            if (searcher != null) {
                try {
                    SearcherManager searcherManager = searcherManagerMap.get(indexDir);
                    if (searcherManager != null) {
                        searcherManager.release(searcher);
                    }
                } catch (Exception e) {
                    log.error("Error releasing IndexSearcher: ", e);
                }
            }
        }
    }

    /**
     * 关键词搜索
     * @param index 索引名称
     * @param word 查询关键词
     * @param columns 被搜索的字段
     * @param current 当前页
     * @param size 每页数据量
     * @return
     */
    public List<Document> search(String index, String word, String[] columns, int current, int size) {
        String indexDir = properties.getIndexDir() + File.separator + index;
        IndexSearcher searcher = null;
        
        try {
            // 获取SearcherManager并获取最新的IndexSearcher
            SearcherManager searcherManager = getSearcherManager(indexDir);
            searcher = searcherManager.acquire();
            
            // TODO: 实际搜索逻辑根据业务需求实现
            
            return List.of();
        } catch (Exception e) {
            log.error("查询 Lucene 错误: ", e);
            return null;
        } finally {
            // 释放IndexSearcher
            if (searcher != null) {
                try {
                    SearcherManager searcherManager = searcherManagerMap.get(indexDir);
                    if (searcherManager != null) {
                        searcherManager.release(searcher);
                    }
                } catch (Exception e) {
                    log.error("Error releasing IndexSearcher: ", e);
                }
            }
        }
    }

    /**
     * 添加文档
     * @param index 索引名称
     * @param document 新的文档
     * @return
     */
    public long addDocument(String index, Document document) {
        String indexDir = properties.getIndexDir() + File.separator + index;
        IndexWriter writer = null;
        
        try {
            // 获取IndexWriter实例
            writer = getIndexWriter(indexDir);

            // 添加文档
            long count = writer.addDocument(document);

            // 提交更改
            writer.commit();
            
            // 更新对应的SearcherManager以获取最新变更
            SearcherManager searcherManager = searcherManagerMap.get(indexDir);
            if (searcherManager != null) {
                searcherManager.maybeRefresh();
            }

            return count;
        } catch (Exception e) {
            log.error("添加 Lucene 文档失败: ", e);
            return 0;
        }
    }

    /**
     * 删除文档
     * @param index 索引名称
     * @param condition 删除条件
     */
    public long deleteDocument(String index, Term condition) {
        String indexDir = properties.getIndexDir() + File.separator + index;
        IndexWriter writer = null;
        
        try {
            // 获取IndexWriter实例
            writer = getIndexWriter(indexDir);

            // 删除文档
            long count = writer.deleteDocuments(condition);

            // 提交更改
            writer.commit();
            
            // 更新对应的SearcherManager以获取最新变更
            SearcherManager searcherManager = searcherManagerMap.get(indexDir);
            if (searcherManager != null) {
                searcherManager.maybeRefresh();
            }

            return count;
        } catch (Exception e) {
            log.error("删除 Lucene 文档错误: ", e);
            return 0;
        }
    }

    /**
     * 更新文档
     * @param index 索引名称
     * @param document 文档
     * @param condition 条件
     * @return
     */
    public long updateDocument(String index, Document document, Term condition) {
        String indexDir = properties.getIndexDir() + File.separator + index;
        IndexWriter writer = null;
        
        try {
            // 获取IndexWriter实例
            writer = getIndexWriter(indexDir);

            // 更新文档
            long count = writer.updateDocument(condition, document);

            // 提交更改
            writer.commit();
            
            // 更新对应的SearcherManager以获取最新变更
            SearcherManager searcherManager = searcherManagerMap.get(indexDir);
            if (searcherManager != null) {
                searcherManager.maybeRefresh();
            }

            return count;
        } catch (Exception e) {
            log.error("更新 Lucene 文档错误: ", e);
            return 0;
        }
    }
}
