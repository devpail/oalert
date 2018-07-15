package com.bingo.oalert.util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class HttpUtils {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static SSLConnectionSocketFactory sslsf = null;
    private static PoolingHttpClientConnectionManager cm = null;
    private static SSLContextBuilder builder = null;

    public static RequestConfig requestConfig = null;

    // 创建CookieStore实例
    static CookieStore cookieStore = null;
    static HttpClientContext context = null;
    public static String valImgUrl = "https://124.133.230.229:8080/OA_WEB/validateImg.do";
    public static String loginCheckUrl = "https://124.133.230.229:8080/OA_WEB/loginCheck.do";
    public static String getCheckDataUrl="https://124.133.230.229:8080/OA_WEB/checkinCal/getPageData.do?empId=716e7770-1470-4561-8e61-6cbff4e297d3&dateYear=2018&dateMonth=7";
    public static String imgPath = "E:\\valcode.jpg";
    static CloseableHttpClient httpclient = null;



    static {
        try {
            builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(HTTP, new PlainConnectionSocketFactory())
                    .register(HTTPS, sslsf)
                    .build();
            cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(200);//max connection
        } catch (Exception e) {
            e.printStackTrace();
        }

        context = HttpClientContext.create();
        cookieStore = new BasicCookieStore();
        requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(60000)
                .setConnectionRequestTimeout(60000).build();
        if(httpclient == null){
            try {
                httpclient = getHttpClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void getOACheckData(){
        System.out.println("---------getValCode");
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(valImgUrl);
            System.out.println("executing request " + httpget.getURI());
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            System.out.println("--------------------------------------");
            downloadJPG(response);
            String userId = "zhangzhaobing";
            String password = "843f73c1ed697407d91d560632219517cde06646ab444924d5e1f84eb323e627168168a1c43c9205b9bef729934d2df324bded4b8a7f204903599ea836bb18df7c0125cf969b5446728e37680c35abe2cb025876eb0f28269b748b854c2ffb84ee17d37d24edf4f74498e1dd2273a68cb22b7741629052154d451c84876d640f";
            String validCode = OcrUtil.ocrImg(imgPath);
            System.out.println(validCode);
            HttpPost httpPost = new HttpPost(loginCheckUrl);
            Map parameterMap = new HashMap();
            parameterMap.put("userId", userId);
            parameterMap.put("password", password);
            parameterMap.put("remember", "false");
            parameterMap.put("validCode", validCode);
            UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(parameterMap), "UTF-8");
            httpPost.setEntity(postEntity);
            System.out.println("request line:" + httpPost.getRequestLine());
            // 执行post请求
            HttpResponse httpResponse = httpclient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                System.out.println(result);
                JSONObject jsonObject = JSON.parseObject(result);
                if("true".equals(jsonObject.get("flag").toString())){
                    System.out.println("登录成功啦");
                }else{
                    System.out.println(jsonObject.get("msg"));
                }


            } else {
                System.out.println("登录失败啦");
            }

            // 创建httpget.
            HttpGet httpget2 = new HttpGet(getCheckDataUrl);
            System.out.println("executing request " + httpget2.getURI());
            // 执行get请求.
            CloseableHttpResponse response2 = httpclient.execute(httpget2);
            statusCode = response2.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response2.getEntity());
                /**解析数据发送提醒*/
                System.out.println("打卡结果:"+ValiUtils.valiCheckinData(result));

            } else {
                System.out.println("查询数据失败啦");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static List<NameValuePair> getParam(Map parameterMap) {
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        Iterator it = parameterMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry parmEntry = (Map.Entry) it.next();
            param.add(new BasicNameValuePair((String) parmEntry.getKey(),
                    (String) parmEntry.getValue()));
        }
        return param;
    }

    public static void getCodeImg(){
        System.out.println("---------getValCode");

        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(valImgUrl);
            System.out.println("executing request " + httpget.getURI());
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("--------------------------------------");
                downloadJPG(response);
                // cookie store
                setCookieStore(response);
                // context
                setContext();
                System.out.println("------------------------------------");
                HttpGet httpget2 = new HttpGet(valImgUrl);
                CloseableHttpResponse response2 = httpclient.execute(httpget2);
                setCookieStore(response);
                // context
                setContext();
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void downloadJPG(HttpResponse httpResponse) throws IOException {
        InputStream input = httpResponse.getEntity().getContent();
        OutputStream output = new FileOutputStream(new File(imgPath));

        IOUtils.copy(input, output);

        if (output != null) {
            output.close();
        }
        output.flush();

    }

    public static CloseableHttpClient getHttpClient() throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm)
                .setConnectionManagerShared(true)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setRedirectStrategy(new DefaultRedirectStrategy())
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
                .build();
        return httpClient;
    }

    public static void setContext() {
        System.out.println("----setContext");
        context = HttpClientContext.create();
        Registry<CookieSpecProvider> registry = RegistryBuilder
                .<CookieSpecProvider> create()
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory()).build();
        context.setCookieSpecRegistry(registry);
        context.setCookieStore(cookieStore);
    }

    public static void setCookieStore(HttpResponse httpResponse) {
        System.out.println("----setCookieStore");
        cookieStore = new BasicCookieStore();
        // JSESSIONID
        String setCookie = httpResponse.getFirstHeader("Set-Cookie")
                .getValue();
        String JSESSIONID = setCookie.substring("JSESSIONID=".length(),
                setCookie.indexOf(";"));
        System.out.println("JSESSIONID:" + JSESSIONID);
        // 新建一个Cookie
        BasicClientCookie cookie = new BasicClientCookie("JSESSIONID",
                JSESSIONID);
        cookie.setPath("/OA_WEB");
        // cookie.setAttribute(ClientCookie.VERSION_ATTR, "0");
        // cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "127.0.0.1");
        // cookie.setAttribute(ClientCookie.PORT_ATTR, "8080");
        // cookie.setAttribute(ClientCookie.PATH_ATTR, "/CwlProWeb");
        cookieStore.addCookie(cookie);
    }


    /**
     * httpClient post请求
     * @param url 请求url
     * @param header 头部信息
     * @param param 请求参数 form提交适用
     * @param entity 请求实体 json/xml提交适用
     * @return 可能为空 需要处理
     * @throws Exception
     *
     */
    public static String post(String  url, Map<String, String> header, Map<String, String> param, HttpEntity entity) throws Exception {
        String result = "";
        CloseableHttpClient httpClient = null;
        try {
            httpClient = getHttpClient();
            HttpPost httpPost = new HttpPost(url);
            // 设置头信息
            if (MapUtils.isNotEmpty(header)) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置请求参数
            if (MapUtils.isNotEmpty(param)) {
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    //给参数赋值
                    formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
            }
            // 设置实体 优先级高
            if (entity != null) {
                httpPost.setEntity(entity);
            }
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                result = EntityUtils.toString(resEntity);
            } else {
                readHttpResponse(httpResponse);
            }
        } catch (Exception e) {throw e;
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        return result;
    }

    public static String readHttpResponse(HttpResponse httpResponse)
            throws ParseException, IOException {
        StringBuilder builder = new StringBuilder();
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
        builder.append("status:" + httpResponse.getStatusLine());
        builder.append("headers:");
        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            builder.append("\t" + iterator.next());
        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            builder.append("response length:" + responseString.length());
            builder.append("response content:" + responseString.replace("\r\n", ""));
        }
        return builder.toString();
    }
}