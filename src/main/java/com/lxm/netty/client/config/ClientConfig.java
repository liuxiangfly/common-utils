/*
 * Copyright 2020 Zhongan.com All right reserved. This software is the
 * confidential and proprietary information of Zhongan.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Zhongan.com.
 */
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
