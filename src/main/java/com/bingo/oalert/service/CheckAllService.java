package com.bingo.oalert.service;


import com.bingo.oalert.util.PersonConst;
import com.bingo.oalert.util.SmsUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @ClassName CheckAllService.java
* @Description 检查所有人是否打卡
* @CreateTime 19-7-6 下午10:46
* @Author bingo
* @Version 1.0.0
*/
@Service
public class CheckAllService extends BaseCheckService{

    private static Logger logger = LoggerFactory.getLogger(CheckAllService.class);

    /** 
    * @Description: 实现项目组成员打卡检查步骤
    * @Author: bingo
    * @Date: 19-7-6 下午10:50
    * @Param: []
    * @return: java.lang.String
     * @param httpClient
     * @param oaReqService
     */
    @Override
    public String doCheck(CloseableHttpClient httpClient, OAReqService oaReqService) throws Exception {

        logger.info("登陆成功，开始获取打卡数据");
        /*获取打卡数据*/
        String data = oaReqService.getOACheckinData(httpClient,true);
        logger.info("开始解析打卡数据");
        /*获取打卡状态*/
        Map<String, String> noCheckinEmpMap = oaReqService.getNoCheckinEmpMap(data);
        String msg = checkAlertSWTMember(noCheckinEmpMap);
        return msg;
    }

    private String checkAlertSWTMember(Map<String, String> noCheckinEmpMap) {
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
            return msg;
        }
        return "全部成员都已打卡～";


    }

}
