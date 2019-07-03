package com.bingo.oalert.util;

import com.bingo.oalert.service.OAReqService;
import org.apache.http.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAReqUtils {
    private static Logger logger = LoggerFactory.getLogger(OAReqUtils.class);

    // 创建CookieStore实例
    public static String isWorkDayUrl = "http://api.goseek.cn/Tools/holiday";
    public static String valImgUrl = "https://124.133.230.229:8080/OA_WEB/validateImg.do";
    public static String loginCheckUrl = "https://124.133.230.229:8080/OA_WEB/loginCheck.do";
    public static String getCheckDataUrl="https://124.133.230.229:8080/OA_WEB/checkinCal/getPageData.do";
    public static String imgPath = "valcode.jpg";

    static{

    }

    public static List<NameValuePair> getLoginParam(){
        String userId = "zhangzhaobing";
        String password = "843f73c1ed697407d91d560632219517cde06646ab444924d5e1f84eb323e627168168a1c43c9205b9bef729934d2df324bded4b8a7f204903599ea836bb18df7c0125cf969b5446728e37680c35abe2cb025876eb0f28269b748b854c2ffb84ee17d37d24edf4f74498e1dd2273a68cb22b7741629052154d451c84876d640f";
        String validCode = OcrUtil.ocrImg(OAReqUtils.imgPath);
        logger.info("验证码："+validCode);

        Map parameterMap = new HashMap();
        parameterMap.put("userId", userId);
        parameterMap.put("password", password);
        parameterMap.put("remember", "false");
        parameterMap.put("validCode", validCode);
        return HttpUtils.getParam(parameterMap);
    }



}
