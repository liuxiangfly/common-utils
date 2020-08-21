package com.lxm.netty.codec.coder;

import io.netty.buffer.ByteBuf;

/**
 * 
 * ClassName: com.lxm.netty.codec.data.EntityDecoder <br/>
 * Function: 实体类解码器，将字节缓冲的数据读出解析为指定实体类 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public interface EntityDecoder<T> {

    T decode(ByteBuf source);
}
