package com.bingo.oalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
public class OalertApplication {

    public static void main(String[] args) {
        SpringApplication.run(OalertApplication.class, args);
    }

    /**
     * 1.创建定时任务在三个时间点检测打卡信息
     * 2.登录oa系统
     * 2.1识别验证码登陆成功（难）
     * 2.2保持session不变
     * 2.3访问打卡信息页面获取打卡信息
     * 2.4解析打卡数据
     * 2.5如果没有及时打卡，发送未打卡提醒
     * */

    @Scheduled(cron = "0 20 8 * * ? ")
    public void checkMorning(){

    }

    @Scheduled(cron = "0 20 12 * * ? ")
    public void checkNoon(){

    }

    @Scheduled(cron = "0 0 18 * * ? ")
    public void checkNight(){

    }


}
