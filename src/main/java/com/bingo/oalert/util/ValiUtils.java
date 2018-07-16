package com.bingo.oalert.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValiUtils {
    private static Logger logger = LoggerFactory.getLogger(ValiUtils.class);
    public static int loginCount = 5;
    static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    static Date mornCkTimeBegin;
    static Date mornCkTimeEnd;
    static Date noonCkTimeBegin;
    static Date noonCkTimeEnd;
    static Date evenCkTimeBegin;
    static Date evenCkTimeEnd;
    static{
        try {
            mornCkTimeBegin = timeFormat.parse("06:00:00");
            mornCkTimeEnd = timeFormat.parse("08:30:00");
            noonCkTimeBegin = timeFormat.parse("11:50:00");
            noonCkTimeEnd = timeFormat.parse("13:30:00");
            evenCkTimeBegin = timeFormat.parse("17:30:00");
            evenCkTimeEnd = timeFormat.parse("20:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static boolean isCheckinTime(Date date) throws ParseException {
        String currentTimeStr = timeFormat.format(date);
        Date currentTime = timeFormat.parse(currentTimeStr);
        /*上午*/
        if(currentTime.compareTo(mornCkTimeBegin)>=0&&currentTime.compareTo(mornCkTimeEnd)<=0){
            return true;
        }
        /*中午*/
        if(currentTime.compareTo(noonCkTimeBegin)>=0&&currentTime.compareTo(noonCkTimeEnd)<=0){
            return true;
        }
        /*下午*/
        if(currentTime.compareTo(evenCkTimeBegin)>=0&&currentTime.compareTo(evenCkTimeEnd)<=0){
            return true;
        }
        return false;
    }


    public static boolean isWorkDay(Date date) throws Exception {
        CloseableHttpClient httpClient = HttpUtils.getHttpClient();
        String url = OAReqUtils.isWorkDayUrl + "?date=" + dateFormat.format(date);
        HttpGet httpGet = new HttpGet(url);
        logger.info("executing request " + httpGet.getURI());
        // 执行get请求.
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        logger.info("--------------------------------------");
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonObject = JSON.parseObject(result);
            int data = jsonObject.getIntValue("data");
            if(data==0){
                return true;
            }
        } else {
            logger.info("查询数据失败啦");
        }
        httpResponse.close();
        return false;
    }

    public static boolean valiCheckinTime(Date checkinTime, Date currentTime){
        String checkTimeStr = timeFormat.format(checkinTime);
        String currentTimeStr = timeFormat.format(currentTime);
        try {
            checkinTime = timeFormat.parse(checkTimeStr);
            currentTime = timeFormat.parse(currentTimeStr);
            /*上午*/
            if(currentTime.compareTo(mornCkTimeBegin)>=0&&currentTime.compareTo(mornCkTimeEnd)<=0){
                if(checkinTime.compareTo(mornCkTimeBegin)>=0&&checkinTime.compareTo(mornCkTimeEnd)<=0){
                    return true;
                }
                return false;
            }
            /*中午*/
            if(currentTime.compareTo(noonCkTimeBegin)>=0&&currentTime.compareTo(noonCkTimeEnd)<=0){
                if(checkinTime.compareTo(noonCkTimeBegin)>=0&&checkinTime.compareTo(noonCkTimeEnd)<=0){
                    return true;
                }
                return false;
            }
            /*下午*/
            if(currentTime.compareTo(evenCkTimeBegin)>=0&&currentTime.compareTo(evenCkTimeEnd)<=0){
                if(checkinTime.compareTo(evenCkTimeBegin)>=0&&checkinTime.compareTo(evenCkTimeEnd)<=0){
                    return true;
                }
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean valiCheckinData(String data) throws IOException {
        data = data.replace("\\\\\\","");
        data = data.replace("\\\"","\"");
        data = data.replace("\"{","{");
        data = data.replace("}\"","}");
        logger.info(data);
        WriteUtils.writeResult(data);
        JSONObject jsonObject = JSON.parseObject(data);
        JSONArray jsonArray = jsonObject.getJSONArray("eventinfo");
        JSONObject checkInfo = null;
        Date currentTime = new Date();
        for(int i=0;i<jsonArray.size();i++){
            checkInfo = jsonArray.getJSONObject(i);
            Date checkinTime = checkInfo.getDate("checkinTime");
            //logger.info(i+":"+dateTimeFormat.format(checkinTime));
            //如果当前日期与今天是同一天
            if(DateUtils.isSameDay(checkinTime, currentTime)){
                if(valiCheckinTime(checkinTime, currentTime)){
                    return true;
                }
            }
        }
        return false;
    }
}
