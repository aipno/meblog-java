package cn.iswxl.meblog.admin.service.impl;

import cn.iswxl.meblog.admin.model.vo.file.FindObjectListRspVO;
import cn.iswxl.meblog.admin.model.vo.file.FindSelectImageRspVO;
import cn.iswxl.meblog.admin.model.vo.file.UploadFileRspVO;
import cn.iswxl.meblog.admin.service.AdminFileService;
import cn.iswxl.meblog.admin.utils.MinioUtil;
import cn.iswxl.meblog.common.domain.dos.ImageDO;
import cn.iswxl.meblog.common.domain.mapper.ImageMapper;
import cn.iswxl.meblog.common.enums.ResponseCodeEnum;
import cn.iswxl.meblog.common.exception.BizException;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AdminFileServiceImpl implements AdminFileService {

    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private ImageMapper imageMapper;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @Override
    public Response uploadFile(MultipartFile file) {
        try {
            // 上传文件
            String url = minioUtil.uploadFile(file);

            // 构建成功返参，将图片的访问链接返回
            return Response.success(UploadFileRspVO.builder().url(url).build());
        } catch (Exception e) {
            log.error("==> 上传文件至 Minio 错误: ", e);
            // 手动抛出业务异常，提示 “文件上传失败”
            throw new BizException(ResponseCodeEnum.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * 获取可复用图片列表
     *
     * @return
     */
    @Override
    public List<FindSelectImageRspVO> findSelectImagePageList() {
        List<ImageDO> imageList = imageMapper.selectAll();

        // DO 转 VO
        return imageList.stream()
                .map(imageDO -> FindSelectImageRspVO.builder()
                        .imageId(imageDO.getId().toString())
                        .imageUrl(imageDO.getImageUrl())
                        .build())
                .toList();
    }

    @Override
    public List<FindObjectListRspVO> findObjectList() {
        try {
            return minioUtil.listObjects();
        } catch (Exception e) {
            log.error("==> 获取桶内所有文件 错误: ", e);
            return new ArrayList<>(); // 异常时返回空列表而不是null，避免NPE
        }
    }
    
    /**
     * 分页获取文件列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    @Override
    public PageResponse findFilePageList(Long current, Long size) {
        Page<ImageDO> page = imageMapper.selectPage(current, size);
        return PageResponse.success(page, page.getRecords());
    }
}
