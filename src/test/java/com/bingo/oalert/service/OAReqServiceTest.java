package com.bingo.oalert.service;

import com.bingo.oalert.util.HttpUtils;
import com.bingo.oalert.util.OAReqUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class OAReqServiceTest {

    private static Logger logger = LoggerFactory.getLogger(OAReqServiceTest.class);

    @Test
    public void getValiCodeImg() throws Exception {
        CloseableHttpClient httpClient = HttpUtils.getHttpClient();
        // 创建httpget.
        HttpGet httpget = new HttpGet(OAReqUtils.valImgUrl);
        logger.info("executing request " + httpget.getURI());
        // 执行get请求.
        CloseableHttpResponse response = httpClient.execute(httpget);
        logger.info("--------------------------------------");
        HttpUtils.downloadJPG(response, OAReqUtils.imgPath);
        response.close();
    }
}