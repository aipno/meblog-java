package cn.iswxl.meblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("t_permission")
public class PermissionDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String permCode;

    private String permName;

    private String module;

    private int parentId;

    private String description;

}
