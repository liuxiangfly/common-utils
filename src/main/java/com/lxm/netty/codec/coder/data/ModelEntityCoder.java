/*
 * Copyright 2020 Zhongan.com All right reserved. This software is the
 * confidential and proprietary information of Zhongan.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Zhongan.com.
 */
package com.lxm.netty.codec.coder.data;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lxm.netty.codec.coder.EntityCoder;

import io.netty.buffer.ByteBuf;

/**
 * ClassName: com.lxm.netty.codec.data.StringEntityCoder <br/>
 * Function: 模型实体解码编码器，解码：将字节缓冲中数据读出解码为指定模型； 编码：将指定模型编码写入字节缓冲 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class ModelEntityCoder<T> implements EntityCoder<T> {
    
    private Charset charset = Charset.forName("utf-8");
    
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();
    
    private Class<T> clazz;
    
    public ModelEntityCoder(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    /* (non-Javadoc)
     * @see com.lxm.netty.codec.data.EntityEncoder#writeTo(java.lang.Object, java.lang.Object)
     */
    @Override
    public void writeTo(T entity, ByteBuf target) {
        String json = gson.toJson(entity);
        target.writeBytes(json.getBytes(charset));
    }

    /* (non-Javadoc)
     * @see com.lxm.netty.codec.data.EntityDecoder#decode(java.lang.Object)
     */
    @Override
    public T decode(ByteBuf source) {
        byte[] bytes = new byte[source.readableBytes()];
        source.readBytes(bytes);
        String json = new String(bytes, charset);
        if(StringUtils.isBlank(json)){
            return null;
        }
        return gson.fromJson(json, clazz);
    }

}
