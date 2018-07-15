package com.bingo.oalert.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bingo.oalert.util.HttpUtils;
import com.bingo.oalert.util.OAReqUtils;
import com.bingo.oalert.util.OcrUtil;
import com.bingo.oalert.util.ValiUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OAReqService {

    public void getValiCodeImg(CloseableHttpClient httpClient) throws IOException {
        // 创建httpget.
        HttpGet httpget = new HttpGet(OAReqUtils.valImgUrl);
        System.out.println("executing request " + httpget.getURI());
        // 执行get请求.
        CloseableHttpResponse response = httpClient.execute(httpget);
        System.out.println("--------------------------------------");
        HttpUtils.downloadJPG(response);
        response.close();
    }

    public void loginOAWEB(CloseableHttpClient httpClient) throws IOException {
        HttpPost httpPost = new HttpPost(OAReqUtils.loginCheckUrl);
        UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(OAReqUtils.getLoginParam(), "UTF-8");
        httpPost.setEntity(postEntity);
        System.out.println("request line:" + httpPost.getRequestLine());
        // 执行post请求
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(httpResponse.getEntity());
            JSONObject jsonObject = JSON.parseObject(result);
            if("true".equals(jsonObject.get("flag").toString())){
                System.out.println("登录成功啦");
            }else{
                System.out.println(jsonObject.get("msg"));
            }
        } else {
            System.out.println("登录失败啦");
        }
        httpResponse.close();
    }

   public String getOACheckinData(CloseableHttpClient httpClient) throws IOException {
       // 创建httpget.
       HttpGet httpGet = new HttpGet(OAReqUtils.getCheckDataUrl);
       System.out.println("executing request " + httpGet.getURI());
       // 执行get请求.
       CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
       int statusCode = httpResponse.getStatusLine().getStatusCode();
       if (statusCode == HttpStatus.SC_OK) {
           String result = EntityUtils.toString(httpResponse.getEntity());
           return result;
       } else {
           System.out.println("查询数据失败啦");
       }
       httpResponse.close();
       return null;
   }

   public boolean valiCheckinStatus(String data){
       /**解析数据发送提醒*/
       boolean valiResult = ValiUtils.valiCheckinData(data);
       System.out.println("打卡结果:" + valiResult);
       return valiResult;
   }

}
