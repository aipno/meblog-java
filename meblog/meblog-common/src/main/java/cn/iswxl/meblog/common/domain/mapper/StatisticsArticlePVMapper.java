package cn.iswxl.meblog.common.domain.mapper;

import cn.iswxl.meblog.common.domain.dos.StatisticsArticlePVDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsArticlePVMapper extends BaseMapper<StatisticsArticlePVDO> {
    /**
     * 对指定日期的文章 PV 访问量进行 +1 (带惰性初始化)
     */
    default void increasePVCount(LocalDate date) {
        // 使用 UPSERT 模式（先尝试更新，如果没有记录则插入）
        int updated = update(null, Wrappers.<StatisticsArticlePVDO>lambdaUpdate()
                .setSql("pv_count = pv_count + 1")
                .eq(StatisticsArticlePVDO::getPvDate, date));

        // 如果没有更新任何记录，说明记录不存在，需要插入新记录
        if (updated == 0) {
            StatisticsArticlePVDO newRecord = StatisticsArticlePVDO.builder()
                    .pvDate(date)
                    .pvCount(1L)
                    .build();
            insert(newRecord);
        }
    }

    /**
     * 查询最近一周的文章 PV 访问量记录
     */
    default List<StatisticsArticlePVDO> selectLatestWeekRecords() {
        return selectList(Wrappers.<StatisticsArticlePVDO>lambdaQuery()
                .le(StatisticsArticlePVDO::getPvDate, LocalDate.now().plusDays(1)) // 小于明天
                .orderByDesc(StatisticsArticlePVDO::getPvDate)
                .last("limit 7")); // 仅查询七条
    }

    /**
     * 检查指定日期的 PV 记录是否存在
     */
    default boolean existsByDate(LocalDate date) {
        return selectCount(Wrappers.<StatisticsArticlePVDO>lambdaQuery()
                .eq(StatisticsArticlePVDO::getPvDate, date)) > 0;
    }

    /**
     * 初始化指定日期的 PV 记录
     */
    default void initPVRecord(LocalDate date) {
        // 检查记录是否已存在
        if (existsByDate(date)) {
            return; // 记录已存在，不重复插入
        }

        // 插入新记录
        StatisticsArticlePVDO articlePVDO = StatisticsArticlePVDO.builder()
                .pvDate(date)
                .pvCount(0L)
                .build();
        insert(articlePVDO);
    }
}
