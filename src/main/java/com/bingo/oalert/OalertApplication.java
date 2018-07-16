package com.bingo.oalert;

import com.bingo.oalert.service.OAReqService;
import com.bingo.oalert.util.HttpUtils;
import com.bingo.oalert.util.SmsUtils;
import com.bingo.oalert.util.ValiUtils;
import org.apache.http.impl.client.CloseableHttpClient;
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

import java.io.IOException;
import java.util.Date;

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
    public String oalertMain(){
        String result="";
        CloseableHttpClient httpClient = null;
        try {
            Date dateTime = new Date();
            /**首先需要检查当前时间是否需要打卡*/
            /*1.检查今天是否是工作日*/
            if(!ValiUtils.isWorkDay(dateTime)){
                return "今天不需要打卡，少侠无须担心！";
            }
            logger.info("今天不是休息日");
            /*2.检查当前时间段是否在打卡时间范围内*/
            if(!ValiUtils.isCheckinTime(dateTime)){
                return "现在不是打卡时间，少侠干别的去吧！";
            }
            logger.info("现在是打卡时间段");
            httpClient = HttpUtils.getHttpClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*获取验证码*/
        try {
            boolean loginSuccess = false;
            /*尝试5次登录OA*/
            for(int i=0; i<ValiUtils.loginCount; i++){
                logger.info("尝试第"+ (i+1) +"次登录");
                oaReqService.getValiCodeImg(httpClient);
                loginSuccess = oaReqService.loginOAWEB(httpClient);
                if(loginSuccess) break;
            }
            /*登录成功后进行后续检查*/
            if(loginSuccess){
                logger.info("登陆成功，开始获取打卡数据");
                /*获取打卡数据*/
                String data = oaReqService.getOACheckinData(httpClient);
                logger.info("开始解析打卡数据");
                /*获取打卡状态*/
                boolean status = oaReqService.valiCheckinStatus(data);
                //TODO 发送短信
                if(status){
                    result = "少侠已经打卡了！";
                }else{
                    result = "少侠请速速打卡！";
                    SmsUtils.sendNoCheckinAlertSms("15098929019");
                }
            }else{
                result = "我试了" + ValiUtils.loginCount + "次都没登录上OA，少侠速速亲自登录OA进行检查！";
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "反正没成功，少侠请从头来过！";
        }
        return result;
    }

    @Scheduled(cron = "0 10 8 * * ? ")
    public void checkMorning(){
        logger.info(oalertMain());
    }

    @Scheduled(cron = "0 55 11 * * ? ")
    public void checkNoon(){
        logger.info(oalertMain());
    }

    @Scheduled(cron = "0 40 17 * * ? ")
    public void checkNight(){
        logger.info(oalertMain());
    }


}
