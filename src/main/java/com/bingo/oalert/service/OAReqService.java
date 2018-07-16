package com.bingo.oalert.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bingo.oalert.util.HttpUtils;
import com.bingo.oalert.util.OAReqUtils;
import com.bingo.oalert.util.ValiUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OAReqService {

    private static Logger logger = LoggerFactory.getLogger(OAReqService.class);
    
    public void getValiCodeImg(CloseableHttpClient httpClient) throws IOException {
        // 创建httpget.
        HttpGet httpget = new HttpGet(OAReqUtils.valImgUrl);
        logger.info("executing request " + httpget.getURI());
        // 执行get请求.
        CloseableHttpResponse response = httpClient.execute(httpget);
        logger.info("--------------------------------------");
        HttpUtils.downloadJPG(response, OAReqUtils.imgPath);
        response.close();
    }

    public boolean loginOAWEB(CloseableHttpClient httpClient) throws IOException {
        HttpPost httpPost = new HttpPost(OAReqUtils.loginCheckUrl);
        UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(OAReqUtils.getLoginParam(), "UTF-8");
        httpPost.setEntity(postEntity);
        logger.info("request line:" + httpPost.getRequestLine());
        // 执行post请求
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonObject = JSON.parseObject(result);
            if("true".equals(jsonObject.get("flag").toString())){
                logger.info("登录成功啦");
                httpResponse.close();
                return true;
            }else{
                logger.info(jsonObject.get("msg")+"");
            }
        } else {
            logger.info("登录失败啦");
        }
        httpResponse.close();
        return false;
    }




   public String getOACheckinData(CloseableHttpClient httpClient) throws IOException {
       // 创建httpget.
       HttpGet httpGet = new HttpGet(OAReqUtils.getCheckDataUrl);
       logger.info("executing request " + httpGet.getURI());
       // 执行get请求.
       CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
       int statusCode = httpResponse.getStatusLine().getStatusCode();
       if (statusCode == HttpStatus.SC_OK) {
           String result = EntityUtils.toString(httpResponse.getEntity());
           return result;
       } else {
           logger.info("查询数据失败啦");
       }
       httpResponse.close();
       return null;
   }

   public boolean valiCheckinStatus(String data) throws IOException {
       /**解析数据发送提醒*/
       boolean valiResult = ValiUtils.valiCheckinData(data);
       logger.info("打卡结果:" + valiResult);
       return valiResult;
   }

}
