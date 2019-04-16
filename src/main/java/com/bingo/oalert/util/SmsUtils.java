package com.bingo.oalert.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ucpass.client.AbsRestClient;
import com.ucpass.client.JsonReqClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SmsUtils {

    private static Logger logger = LoggerFactory.getLogger(SmsUtils.class);

    static AbsRestClient InstantiationRestAPI() {
        return new JsonReqClient();
    }

    /*发送消息通用方法*/
    public static boolean sendSms(String mobile, String templateid, String smsMsg){
        String sid = "a66036bd729830dd48f7dbd1899d1316";
        String token = "1dd052184f312be7449cb334686b3f65";
        String appid = "ddb5e974a5b645659617bd3ed71e9f6d";
        String uid = "";
        try {
            String result=InstantiationRestAPI().sendSms(sid, token, appid, templateid, smsMsg, mobile, uid);
            logger.info("Response content is: " + result);
            JSONObject jsonObject = JSON.parseObject(result);
            String code = jsonObject.getString("code");
            if("000000".equals(code)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**发送没有打卡考勤提醒*/
    public static void sendNoCheckinAlertSms(String mobile){
        String templateid = "350873";
        sendSms(mobile,templateid,"");
    }

    /**发送没有打卡考勤提醒*/
    public static void sendNoCheckinAlertSms(String mobile, String paramMsg){
        String templateid = "359545";
        sendSms(mobile,templateid,paramMsg);
    }

    /**发送打卡考勤检测失败提醒*/
    public static void sendCheckinFailedSms(String mobile, String paramMsg){
        String templateid = "441300";
        sendSms(mobile,templateid,paramMsg);
    }

    public static void sendSms(String sid, String token, String appid, String templateid, String param, String mobile, String uid){
        try {
            String result=InstantiationRestAPI().sendSms(sid, token, appid, templateid, param, mobile, uid);
            logger.info("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendSmsBatch(String sid, String token, String appid, String templateid, String param, String mobile, String uid){
        try {
            String result=InstantiationRestAPI().sendSmsBatch(sid, token, appid, templateid, param, mobile, uid);
            logger.info("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addSmsTemplate(String sid, String token, String appid, String type, String template_name, String autograph, String content){
        try {
            String result=InstantiationRestAPI().addSmsTemplate(sid, token, appid, type, template_name, autograph, content);
            logger.info("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getSmsTemplate(String sid, String token, String appid, String templateid, String page_num, String page_size){
        try {
            String result=InstantiationRestAPI().getSmsTemplate(sid, token, appid, templateid, page_num, page_size);
            logger.info("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void editSmsTemplate(String sid, String token, String appid, String templateid, String type, String template_name, String autograph, String content){
        try {
            String result=InstantiationRestAPI().editSmsTemplate(sid, token, appid, templateid, type, template_name, autograph, content);
            logger.info("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void deleterSmsTemplate(String sid, String token, String appid, String templateid){
        try {
            String result=InstantiationRestAPI().deleterSmsTemplate(sid, token, appid, templateid);
            logger.info("Response content is: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 测试说明  启动main方法后，请在控制台输入数字(数字对应 相应的调用方法)，回车键结束
     * 参数名称含义，请参考rest api 文档
     * @throws IOException
     * @method main
     */
    public static void main(String[] args) throws IOException{

        logger.info("请输入方法对应的数字(例如1),Enter键结束:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String methodNumber = br.readLine();

        if (StringUtils.isBlank(methodNumber)){
            logger.info("请输入正确的数字，不可为空");
            return;
        }

        if (methodNumber.equals("1")) {  //指定模板单发
            String sid = "a66036bd729830dd48f7dbd1899d1316";
            String token = "1dd052184f312be7449cb334686b3f65";
            String appid = "ddb5e974a5b645659617bd3ed71e9f6d";
            String templateid = "350873";
            String param = "";
            String mobile = "15098926019";
            String uid = "";
            sendSms(sid, token, appid, templateid, param, mobile, uid);
        } else if (methodNumber.equals("2")) { //指定模板群发
            String sid = "";
            String token = "";
            String appid = "";
            String templateid = "";
            String param = "";
            String mobile = "";
            String uid = "";
            sendSmsBatch(sid, token, appid, templateid, param, mobile, uid);
        } else if (methodNumber.equals("3")) {  //增加模板
            String sid = "";
            String token = "";
            String appid = "";
            String type = "";
            String template_name = "";
            String autograph = "";
            String content = "";
            addSmsTemplate(sid, token, appid, type, template_name, autograph, content);
        } else if (methodNumber.equals("4")) {  //查询模板
            String sid = "";
            String token = "";
            String appid = "";
            String templateid = "";
            String page_num = "";
            String page_size = "";
            getSmsTemplate(sid, token, appid, templateid, page_num, page_size);
        } else if (methodNumber.equals("5")) {  //编辑模板
            String sid = "";
            String token = "";
            String appid = "";
            String templateid = "";
            String type = "";
            String template_name = "";
            String autograph = "";
            String content = "";
            editSmsTemplate(sid, token, appid, templateid, type, template_name, autograph, content);
        } else if (methodNumber.equals("6")) {  //删除模板
            String sid = "";
            String token = "";
            String appid = "";
            String templateid = "";
            deleterSmsTemplate(sid, token, appid, templateid);
        }
    }
}
