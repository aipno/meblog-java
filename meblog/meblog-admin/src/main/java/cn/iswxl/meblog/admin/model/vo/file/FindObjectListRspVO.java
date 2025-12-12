package cn.iswxl.meblog.admin.model.vo.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindObjectListRspVO {

    private int  id;

    private String fileName;

    private String url;

    private String fileSize;

    private String contentType;
}
