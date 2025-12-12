package cn.iswxl.meblog.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@ComponentScan({"cn.iswxl.meblog"}) // 多模块项目中，必需手动指定扫描 cn.iswxl.meblog 包下面的所有类
@EnableScheduling // 启用定时任务
public class MeblogWebApplication {

    public static void main(String[] args) {

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

        SpringApplication.run(MeblogWebApplication.class, args);
    }

}
