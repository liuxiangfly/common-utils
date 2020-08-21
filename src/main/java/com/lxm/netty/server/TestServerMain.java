/*
 * Copyright 2020 Zhongan.com All right reserved. This software is the
 * confidential and proprietary information of Zhongan.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Zhongan.com.
 */
package com.lxm.netty.server;

import com.lxm.netty.server.entitytype.EntityTypeRegistry;

/**
 * ClassName: com.lxm.netty.server.TestServerMain <br/>
 * Function: FUNCTION <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class TestServerMain {
    
    public static void main(String[] args) {
        EntityTypeRegistry.registryEntityType(); // 注册类型编码解码器
        NettyServer nettyServer = new NettyServer(8088);
        nettyServer.start();
    }

}
