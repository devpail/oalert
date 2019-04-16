package com.bingo.oalert;

import com.bingo.oalert.service.OAReqService;
import com.bingo.oalert.util.HttpUtils;
import com.bingo.oalert.util.PersonConst;
import com.bingo.oalert.util.SmsUtils;
import com.bingo.oalert.util.ValiUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
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
import java.util.List;
import java.util.Map;

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
                logger.info("现在不是打卡时间");
            }else{
                logger.info("现在是打卡时间段");
            }

            httpClient = HttpUtils.getHttpClient();
        } catch (Exception e) {
            e.printStackTrace();
            SmsUtils.sendCheckinFailedSms("15098929019","程序出现错误");

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
                String data = oaReqService.getOACheckinData(httpClient, false);
                logger.info("开始解析打卡数据");
                /*获取打卡状态*/
                boolean status = oaReqService.valiCheckinStatus(data);
                //发送短信
                if(status){
                    result = "少侠已经打卡了！";
                }else{
                    result = "少侠请速速打卡！";
                    SmsUtils.sendNoCheckinAlertSms("15098929019");
                }
            }else{
                result = "我试了" + ValiUtils.loginCount + "次都没登录上OA，少侠速速亲自登录OA进行检查！";
                SmsUtils.sendCheckinFailedSms("15098929019","登录OA失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "反正没成功，少侠请从头来过！";
            SmsUtils.sendCheckinFailedSms("15098929019","程序出现错误");
        }
        return result;
    }


    @RequestMapping(value="checkAll")
    @ResponseBody
    public Object oalertAllMain(){
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
            httpClient = HttpUtils.getHttpClient();
        } catch (Exception e) {
            e.printStackTrace();
            SmsUtils.sendCheckinFailedSms("15098929019","程序出现错误");

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
                String data = oaReqService.getOACheckinData(httpClient,true);
                logger.info("开始解析打卡数据");
                /*获取打卡状态*/
                Map<String, String> noCheckinEmpMap = oaReqService.getNoCheckinEmpMap(data);
                checkAlertSWTMember(noCheckinEmpMap);
                return noCheckinEmpMap;
            }else{
                result = "我试了" + ValiUtils.loginCount + "次都没登录上OA，少侠速速亲自登录OA进行检查！";
                SmsUtils.sendCheckinFailedSms("15098929019","登录OA失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "反正没成功，少侠请从头来过！";
            SmsUtils.sendCheckinFailedSms("15098929019","程序出现错误");
        }
        return result;
    }

    private void checkAlertSWTMember(Map<String, String> noCheckinEmpMap) {
        /**
         * 1.遍历未打卡人员记录
         * 2.查询检测人员列表
         * 3.如果检测到人员未打卡则短信提醒
         * */
        List<String> alertList = Lists.newArrayList();
        for(String empId : PersonConst.PERSOPN_LIST){
            if(noCheckinEmpMap.containsKey(empId)){
                alertList.add(noCheckinEmpMap.get(empId));
                SmsUtils.sendNoCheckinAlertSms(PersonConst.PHONE_MAP.get(empId));
                logger.info(noCheckinEmpMap.get(empId)+"未打卡！");
            }
        }
        if(alertList.size() >= 1){
            String msg = StringUtils.join(alertList,"-");
            SmsUtils.sendNoCheckinAlertSms("15098929019", msg);
        }


    }

    @Scheduled(cron = "0 25 8 * * ? ")
    public void checkMorning(){
        oalertAllMain();
    }

    @Scheduled(cron = "0 30 12 * * ? ")
    public void checkNoon(){
        oalertAllMain();
    }

    @Scheduled(cron = "0 45 17 * * ? ")
    public void checkNight(){
        oalertAllMain();
    }


}
