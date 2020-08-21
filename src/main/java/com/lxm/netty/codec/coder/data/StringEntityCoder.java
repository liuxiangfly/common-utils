/*
 * Copyright 2020 Zhongan.com All right reserved. This software is the
 * confidential and proprietary information of Zhongan.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Zhongan.com.
 */
package com.lxm.netty.codec.coder.data;

import java.nio.charset.Charset;

import com.lxm.netty.codec.coder.EntityCoder;

import io.netty.buffer.ByteBuf;

/**
 * ClassName: com.lxm.netty.codec.data.StringEntityCoder <br/>
 * Function: String实体解码编码器，解码：将字节缓冲中数据读出解码为String； 编码：将String编码写入字节缓冲 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class StringEntityCoder implements EntityCoder<String> {

    private Charset charset = Charset.forName("utf-8");
    
    /* (non-Javadoc)
     * @see com.lxm.netty.codec.data.EntityEncoder#writeTo(java.lang.Object, java.lang.Object)
     */
    @Override
    public void writeTo(String entity, ByteBuf target) {
        target.writeBytes(entity.getBytes(charset));
    }

    /* (non-Javadoc)
     * @see com.lxm.netty.codec.data.EntityDecoder#decode(java.lang.Object)
     */
    @Override
    public String decode(ByteBuf source) {
        byte[] bytes = new byte[source.readableBytes()];
        source.readBytes(bytes);
        return new String(bytes, charset);
    }

}
