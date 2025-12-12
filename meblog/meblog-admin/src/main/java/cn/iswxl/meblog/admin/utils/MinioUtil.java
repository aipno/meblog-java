package cn.iswxl.meblog.admin.utils;

import cn.iswxl.meblog.admin.config.MinioProperties;
import cn.iswxl.meblog.admin.model.vo.file.FindObjectListRspVO;
import cn.iswxl.meblog.common.domain.dos.FileDO;
import cn.iswxl.meblog.common.domain.mapper.FileMapper;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class MinioUtil {

    // 允许的文件后缀白名单
    private static final Set<String> ALLOWED_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", // 图片格式
            ".pdf", // PDF文档
            ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", // Office文档
            ".txt", ".md" // 文本文件
    ));

    // 文件头(Magic Number)映射
    private static final Map<String, String> FILE_HEADER_MAP = new HashMap<>();

    static {
        // 图片文件头
        FILE_HEADER_MAP.put("FFD8FFE0", ".jpg");
        FILE_HEADER_MAP.put("FFD8FFE1", ".jpg");
        FILE_HEADER_MAP.put("FFD8FFE8", ".jpg");
        FILE_HEADER_MAP.put("89504E47", ".png");
        FILE_HEADER_MAP.put("47494638", ".gif");
        FILE_HEADER_MAP.put("424D", ".bmp");

        // 文档文件头
        FILE_HEADER_MAP.put("25504446", ".pdf");
        FILE_HEADER_MAP.put("504B0304", ".docx"); // Also .xlsx, .pptx
        FILE_HEADER_MAP.put("D0CF11E0", ".doc");  // Also .xls, .ppt
    }

    @Value("${spring.profiles.active}")
    private String env;
    @Value("${minio.external}")
    private String external;
    @Value("${minio.bucketName}")
    private String bucketName;

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioClient minioClient;
    
    @Autowired
    private FileMapper fileMapper;

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadFile(MultipartFile file) throws Exception {
        // 判断文件是否为空
        if (file == null || file.getSize() == 0) {
            log.error("==> 上传文件异常：文件大小为空 ...");
            throw new RuntimeException("文件大小不能为空");
        }

        // 文件的原始名称
        String originalFileName = file.getOriginalFilename();
        // 文件的 Content-Type
        String contentType = file.getContentType();

        // 获取并验证文件后缀
        String suffix = getFileExtension(originalFileName);
        if (!isAllowedFileExtension(suffix)) {
            log.error("==> 上传文件异常：不允许的文件类型, 文件名: {}, 后缀: {}", originalFileName, suffix);
            throw new RuntimeException("不允许上传此类型的文件: " + suffix);
        }

        // 验证文件头(魔术数字)
        if (!isValidFileHeader(file.getInputStream(), suffix)) {
            log.error("==> 上传文件异常：文件类型与声明类型不符, 文件名: {}", originalFileName);
            throw new RuntimeException("文件类型验证失败");
        }

        // 生成存储对象的名称（将 UUID 字符串中的 - 替换成空字符串）
        String key = UUID.randomUUID().toString().replace("-", "");

        // 拼接上文件后缀，即为要存储的文件名
        String objectName = String.format("%s%s", key, suffix);

        log.info("==> 开始上传文件至 Minio, ObjectName: {}", objectName);

        // 上传文件至 Minio
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(contentType)
                .build());

        // 返回文件的访问链接
        String url = "";

        if (Objects.equals(env, "dev")) {
            url = String.format("%s/%s/%s", minioProperties.getEndpoint(), minioProperties.getBucketName(), objectName);
        } else if (Objects.equals(env, "prod")) {
            url = String.format(external + "/%s/%s", minioProperties.getBucketName(), objectName);
        }
        
        // 将文件信息保存到数据库
        FileDO fileDO = FileDO.builder()
                .fileName(objectName)
                .url(url)
                .fileSize(String.valueOf(file.getSize()))
                .contentType(contentType)
                .createTime(LocalDateTime.now())
                .build();
        fileMapper.insert(fileDO);
        
        log.info("==> 上传文件至 Minio 成功，访问路径: {}", url);
        return url;
    }


    /**
     * 获取桶内所有文件（已废弃，仅用于初始化同步）
     *
     * @return
     * @throws Exception
     * @deprecated 使用分页查询数据库代替全量加载
     */
    @Deprecated
    public List<FindObjectListRspVO> listObjects() throws Exception {

        List<FindObjectListRspVO> objectList = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .build());

            for (Result<Item> result : results) {
                try {
                    Item item = result.get();
                    if (item == null || item.objectName() == null) {
                        continue; // 忽略无效项
                    }

                    String fileName = item.objectName();
                    String url = buildUrl(fileName); // 构建 URL 更加规范
                    String contentType = getFileContentType(bucketName, fileName);
                    String fileSize = String.valueOf(item.size());

                    FindObjectListRspVO vo = FindObjectListRspVO.builder()
                            .id(objectList.size())
                            .fileName(fileName)
                            .url(url)
                            .fileSize(fileSize)
                            .contentType(contentType)
                            .build();

                    objectList.add(vo);
                } catch (Exception innerEx) {
                    log.warn("处理单个对象时发生错误，已跳过该项: {}", innerEx.getMessage(), innerEx);
                    // 不抛出异常，仅记录警告并继续处理下一个对象
                }
            }
        } catch (Exception e) {
            throw new Exception("获取对象列表失败", e);
        }

        return objectList;
    }

    // 辅助方法：构建 URL
    private String buildUrl(String objectName) {
        try {
            URI uri = new URI(external).resolve(bucketName + "/" + objectName);
            return uri.toString();
        } catch (URISyntaxException e) {
            log.warn("URL 构造失败，回退至原始拼接方式: {}", e.getMessage());
            return external + "/" + bucketName + "/" + objectName;
        }
    }

    // 辅助方法：获取文件 Content-Type
    private String getFileContentType(String bucketName, String objectName) {
        try {
            // 获取对象的元数据
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            // 从元数据中获取Content-Type
            return stat.contentType();

        } catch (Exception e) {
            log.warn("获取文件Content-Type失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名，如果无扩展名则返回空字符串
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ""; // 没有扩展名
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 检查文件扩展名是否在白名单中
     *
     * @param extension 文件扩展名
     * @return 是否允许
     */
    private boolean isAllowedFileExtension(String extension) {
        return ALLOWED_FILE_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * 验证文件头是否与声明的文件类型一致
     *
     * @param inputStream 文件输入流
     * @param extension   文件扩展名
     * @return 是否有效
     */
    private boolean isValidFileHeader(InputStream inputStream, String extension) {
        try {
            // 读取文件头部字节
            byte[] header = new byte[8];
            int read = inputStream.read(header);
            if (read <= 0) {
                return false;
            }

            // 重置流位置以便后续使用
            inputStream.reset();

            // 将字节转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < read; i++) {
                sb.append(String.format("%02X", header[i]));
            }
            String fileHead = sb.toString();

            // 检查是否匹配预期的文件头
            for (Map.Entry<String, String> entry : FILE_HEADER_MAP.entrySet()) {
                if (fileHead.startsWith(entry.getKey()) && extension.equals(entry.getValue())) {
                    return true;
                }
            }

            // 如果没有预定义的文件头，暂时认为有效（对于不在检查范围内的文件类型）
            return true;
        } catch (IOException e) {
            log.error("读取文件头失败", e);
            return false;
        }
    }
}