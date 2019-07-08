package com.bingo.oalert;

import com.bingo.oalert.service.BaseCheckService;
import com.bingo.oalert.service.OAReqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@EnableScheduling
@Controller
@RequestMapping(value="/")
public class OalertApplication {

    public static void main(String[] args) {
        SpringApplication.run(OalertApplication.class, args);
    }

    private static Logger logger = LoggerFactory.getLogger(OalertApplication.class);

    @Autowired
    private OAReqService oaReqService;

    @Autowired
    private BaseCheckService checkMeService;

    @Autowired
    private BaseCheckService checkAllService;

    @RequestMapping(value="")
    @ResponseBody
    public String index(){
        return "index";
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
    @RequestMapping(value="check")
    @ResponseBody
    public Object oalertMe(){
        Object result = checkMeService.checkMain();
        return result;
    }


    @RequestMapping(value="checkAll")
    @ResponseBody
    public Object oalertAll(){
        Object result = checkAllService.checkMain();
        return result;
    }



    @Scheduled(cron = "0 25 8 * * ? ")
    public void checkMorning(){
        oalertAll();
    }

    @Scheduled(cron = "0 30 12 * * ? ")
    public void checkNoon(){
        oalertAll();
    }

    @Scheduled(cron = "0 45 17 * * ? ")
    public void checkNight(){
        oalertAll();
    }


}
