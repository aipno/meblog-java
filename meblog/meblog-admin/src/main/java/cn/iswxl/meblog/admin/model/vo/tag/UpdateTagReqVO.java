package cn.iswxl.meblog.admin.model.vo.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTagReqVO {
    /**
     * 标签 ID
     */
    private Long id;
    /**
     * 标签名称
     */
    private String name;
}
