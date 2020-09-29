package com.lxm.rest.async;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * 
 * ClassName: com.lxm.rest.async.AsyncRestTemplateUtil <br/>
 * Function: rest异步请求工具类 <br/>
 * Date: 2020年9月29日 <br/>
 * @author liuxiangming
 */
public class AsyncRestTemplateUtil {

    private static final Logger logger = LoggerFactory.getLogger(AsyncRestTemplateUtil.class);
    

    private int               connectTimeout = -1;

    private int               readTimeout    = -1;

    private AsyncRestTemplate asyncRestTemplate;
    
    private AsyncClientHttpRequestFactory requestFactory;

    public void init() {
        if(requestFactory == null){
            requestFactory = new Netty4ClientHttpRequestFactory();
            ((Netty4ClientHttpRequestFactory) requestFactory).setConnectTimeout(connectTimeout);
            ((Netty4ClientHttpRequestFactory) requestFactory).setReadTimeout(readTimeout);
        }
        asyncRestTemplate = new AsyncRestTemplate(requestFactory);
        // 定义附加的HTTP消息转换器
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(
                Charset.forName("UTF-8")); // 支持UTF-8或者自定义的编码，StringHttpMessageConverter默认编码会使用ISO-8859-1
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        List<HttpMessageConverter<?>> messageConverters = asyncRestTemplate.getMessageConverters();
        messageConverters.add(1, stringHttpMessageConverter);
    }
    
    /**
     * 异步请求处理
     * @param url
     * @param method  请求方法（get/post）
     * @param headers 请求头信息
     * @param body 请求休
     * @param responseClazz 响应类型
     * @param successCallback 请求成功回调
     * @param failureCallback 请求失败回调
     * @return
     */
    public <T> ListenableFuture<ResponseEntity<T>> exchange(String url, String method, Map<String, String> headers, String body, Class<T> responseClazz, SuccessCallback<ResponseEntity<T>> successCallback, FailureCallback failureCallback){
        HttpMethod httpMethod = convertMethod(method);
        HttpHeaders headerList = new HttpHeaders();
        if (headers != null) {
            headerList.setAll(headers);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headerList);
        ListenableFuture<ResponseEntity<T>> future = asyncRestTemplate.exchange(url, httpMethod, requestEntity , responseClazz);
        SuccessCallback<ResponseEntity<T>> successCall = successCallback;
        FailureCallback failureCall = failureCallback;
        if(successCall == null){
            successCall = result -> logger.info("request success, url:{}", url);
        }
        if(failureCall == null){
            failureCall = ex -> logger.error("request exception, url:{}, exception:", url, ex);
        }
        future.addCallback(successCall, failureCall);
        return future;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public AsyncRestTemplate getAsyncRestTemplate() {
        return asyncRestTemplate;
    }

    public void setAsyncRestTemplate(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate;
    }

    public AsyncClientHttpRequestFactory getRequestFactory() {
        return requestFactory;
    }

    public void setRequestFactory(AsyncClientHttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    private HttpMethod convertMethod(String method){
        HttpMethod httpMethod = null;
        if("get".equalsIgnoreCase(method.trim())){
            httpMethod = HttpMethod.GET;
        }else if("post".equalsIgnoreCase(method.trim())){
            httpMethod = HttpMethod.POST;
        }else{
            throw new RuntimeException(method + " is not surpport.");
        }
        return httpMethod;
    }

}
