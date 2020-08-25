package com.lxm.netty.client.config;

/**
 * ClassName: com.lxm.netty.client.config.ClientConfig <br/>
 * Function: FUNCTION <br/>
 * Date: 2020年8月20日 <br/>
 * @author liuxiangming
 */
public class ClientConfig {
    
    /**
     * 请求超时，单位：ms
     */
    private static int requestTimeout = 500;
    
    /**
     * 连接超时，单位：ms
     */
    private static int connectTimeout = 10000;

    public static int getRequestTimeout() {
        return requestTimeout;
    }

    public static void setRequestTimeout(int requestTimeout) {
        ClientConfig.requestTimeout = requestTimeout;
    }

    public static int getConnectTimeout() {
        return connectTimeout;
    }

    public static void setConnectTimeout(int connectTimeout) {
        ClientConfig.connectTimeout = connectTimeout;
    }
    
    

}
