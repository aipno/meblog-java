package cn.iswxl.meblog.admin.schedule;

import cn.iswxl.meblog.common.domain.mapper.StatisticsArticlePVMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
public class InitPVRecordScheduledTask {

    @Autowired
    private StatisticsArticlePVMapper articlePVMapper;

    @Scheduled(cron = "0 0 23 * * ?") // 每天晚间 23 点执行
    public void execute() {
        log.info("==> 开始执行初始化明日 PV 访问量记录定时任务");

        // 当日日期
        LocalDate currDate = LocalDate.now();

        // 明日
        LocalDate tomorrowDate = currDate.plusDays(1);

        // 初始化明日 PV 记录（如果已存在则不会重复插入）
        articlePVMapper.initPVRecord(tomorrowDate);
        log.info("==> 结束执行初始化明日 PV 访问量记录定时任务");
    }
    
    /**
     * 每小时检查一次当日 PV 记录（作为备用保障机制）
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行一次
    public void checkTodayPVRecord() {
        LocalDate today = LocalDate.now();
        articlePVMapper.initPVRecord(today);
    }
}
