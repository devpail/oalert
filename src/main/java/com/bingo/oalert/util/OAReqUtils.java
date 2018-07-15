package com.bingo.oalert.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.*;

public class OAReqUtils {

    // 创建CookieStore实例
    static CookieStore cookieStore = null;
    static HttpClientContext context = null;
    public static String valImgUrl = "https://124.133.230.229:8080/OA_WEB/validateImg.do";
    public static String loginCheckUrl = "https://124.133.230.229:8080/OA_WEB/loginCheck.do";
    public static String getCheckDataUrl="https://124.133.230.229:8080/OA_WEB/checkinCal/getPageData.do?empId=716e7770-1470-4561-8e61-6cbff4e297d3&dateYear=2018&dateMonth=7";
    public static String imgPath = "E:\\valcode.jpg";

    public static List<NameValuePair> getLoginParam(){
        String userId = "zhangzhaobing";
        String password = "843f73c1ed697407d91d560632219517cde06646ab444924d5e1f84eb323e627168168a1c43c9205b9bef729934d2df324bded4b8a7f204903599ea836bb18df7c0125cf969b5446728e37680c35abe2cb025876eb0f28269b748b854c2ffb84ee17d37d24edf4f74498e1dd2273a68cb22b7741629052154d451c84876d640f";
        String validCode = OcrUtil.ocrImg(OAReqUtils.imgPath);
        System.out.println(validCode);

        Map parameterMap = new HashMap();
        parameterMap.put("userId", userId);
        parameterMap.put("password", password);
        parameterMap.put("remember", "false");
        parameterMap.put("validCode", validCode);
        return HttpUtils.getParam(parameterMap);
    }

    public static void getValiCodeImg(CloseableHttpClient httpclient){


    }


}
