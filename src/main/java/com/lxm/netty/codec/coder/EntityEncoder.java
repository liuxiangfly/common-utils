package com.lxm.netty.codec.coder;

import io.netty.buffer.ByteBuf;

/**
 * 
 * ClassName: com.lxm.netty.codec.data.EntityEncoder <br/>
 * Function: 实体类编码器，将实体类编码写入字节缓冲中 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public interface EntityEncoder<T> {

    void writeTo(T entity, ByteBuf target);
}
