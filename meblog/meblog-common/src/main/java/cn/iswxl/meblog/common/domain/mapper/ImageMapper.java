package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.ImageDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface ImageMapper extends BaseMapper<ImageDO> {

    default Long insertImage(String url) {
        LambdaQueryWrapper<ImageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImageDO::getImageUrl, url);
        ImageDO imageUrl;
        if (selectOne(queryWrapper) == null) {
            // VO 转 DO
            imageUrl = new ImageDO();
            imageUrl.setImageUrl(url);

            insert(imageUrl);
        } else {
            imageUrl = selectOne(queryWrapper);
        }
        return imageUrl.getId();
    }

    default List<ImageDO> selectAll() {
        return selectList(null);
    }
    
    /**
     * 分页查询文件列表
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    default Page<ImageDO> selectPage(long current, long size) {
        Page<ImageDO> page = new Page<>(current, size);
        return selectPage(page, null);
    }
}
