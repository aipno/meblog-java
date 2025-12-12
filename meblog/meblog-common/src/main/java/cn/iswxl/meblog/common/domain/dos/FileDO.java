package cn.iswxl.meblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_file")
public class FileDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String fileName;

    private String url;

    private String fileSize;

    private String contentType;

    private LocalDateTime createTime;
}
