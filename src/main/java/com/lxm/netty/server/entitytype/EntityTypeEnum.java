package com.lxm.netty.server.entitytype;

import com.lxm.netty.codec.coder.EntityCoder;
import com.lxm.netty.codec.coder.data.StringEntityCoder;
import com.lxm.netty.server.processor.RequestProcessor;
import com.lxm.netty.server.processor.StringRequestProcessor;

/**
 * 
 * ClassName: com.lxm.netty.server.entitytype.EntityTypeEnum <br/>
 * Function: 实体类型枚举  <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public enum EntityTypeEnum {
    
    STRING_TYPE(1, new StringEntityCoder(), new StringRequestProcessor());
    
    @SuppressWarnings("rawtypes")
    private EntityTypeEnum(int type, EntityCoder coder, RequestProcessor processor) {
        this.type = type;
        this.coder = coder;
        this.processor = processor;
    }

    /**
     * 实体类型
     */
    private int type;
    
    /**
     * 实体类型编码解码器
     */
    @SuppressWarnings("rawtypes")
    private EntityCoder coder;
    
    /**
     * 实体类型处理器
     */
    @SuppressWarnings("rawtypes")
    private RequestProcessor processor;

    public int getType() {
        return type;
    }

    @SuppressWarnings("rawtypes")
    public EntityCoder getCoder() {
        return coder;
    }
    
    public RequestProcessor getProcessor() {
        return processor;
    }

}
