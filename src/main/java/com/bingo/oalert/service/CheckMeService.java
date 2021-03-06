package com.bingo.oalert.service;

import com.bingo.oalert.util.SmsUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
* @Description 测试自己是否打卡
* @ClassName CheckMeService.java
* @CreateTime 19-7-8 下午2:31
* @Author bingo
* @Version 1.0.0
*/
@Service
public class CheckMeService extends BaseCheckService {

    private static Logger logger = LoggerFactory.getLogger(CheckMeService.class);


    @Override
    public String doCheck(CloseableHttpClient httpClient, OAReqService oaReqService) throws IOException {
        String result;
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
        return result;
    }
}
