package cn.iswxl.meblog.admin.service;

import cn.iswxl.meblog.admin.model.vo.file.FindObjectListRspVO;
import cn.iswxl.meblog.admin.model.vo.file.FindSelectImageRspVO;
import cn.iswxl.meblog.common.utils.PageResponse;
import cn.iswxl.meblog.common.utils.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminFileService {

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    Response uploadFile(MultipartFile file);

    /**
     * 获取可复用图片列表
     *
     * @return
     */
    List<FindSelectImageRspVO> findSelectImagePageList();

    List<FindObjectListRspVO> findObjectList();

    /**
     * 分页获取文件列表
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    PageResponse findFilePageList(Long current, Long size);
}
