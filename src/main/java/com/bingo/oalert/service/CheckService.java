package com.bingo.oalert.service;

import com.bingo.oalert.util.HttpUtils;
import com.bingo.oalert.util.SmsUtils;
import com.bingo.oalert.util.ValiUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


public abstract class CheckService {

    private static Logger logger = LoggerFactory.getLogger(CheckService.class);

    @Autowired
    private OAReqService oaReqService;

    public Object checkMain(){
        String result="";
        CloseableHttpClient httpClient = null;
        Date dateTime = new Date();
        try {
            /**首先需要检查当前时间是否需要打卡*/
            /*1.检查今天是否是工作日*/
            int checkCount = 0;
            Boolean checkResult;
            do {
                checkResult = ValiUtils.isWorkDay(dateTime);
            } while (checkResult == null && checkCount++ < 3);
            if (checkCount == 3){
                result = "重试3次请求工作日接口失败！";
                logger.error(result);
                SmsUtils.sendCheckinFailedSms("15098929019","程序出现错误");
                return result;
            }
            if(!checkResult){
                result = "今天不需要打卡，少侠无须担心！";
                logger.info(result);
                //return result;
            }
            logger.info("今天不是休息日");
        } catch (Exception e) {
            e.printStackTrace();
            SmsUtils.sendCheckinFailedSms("15098929019","程序出现错误");
            return "程序出现错误";
        }
        /*获取验证码*/
        try {
            httpClient = HttpUtils.getHttpClient();
            boolean loginSuccess = false;
            /*尝试5次登录OA*/
            for(int i=0; i<ValiUtils.loginCount; i++){
                logger.info("尝试第"+ (i+1) +"次登录");
                oaReqService.getValiCodeImg(httpClient);
                loginSuccess = oaReqService.loginOAWEB(httpClient);
                if(loginSuccess) {
                    break;
                }
            }
            /*登录成功后进行后续检查*/
            if(loginSuccess){
                result = (String) doCheck(httpClient,oaReqService);
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

    public abstract Object doCheck(CloseableHttpClient httpClient, OAReqService oaReqService) throws Exception;

}
