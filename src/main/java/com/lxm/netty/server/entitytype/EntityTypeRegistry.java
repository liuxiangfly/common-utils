package com.lxm.netty.server.entitytype;

import com.lxm.netty.codec.registry.EntityDecoderRegistry;
import com.lxm.netty.codec.registry.EntityEncoderRegistry;
import com.lxm.netty.server.processor.RequestProcessorRegistry;

/**
 * 
 * ClassName: com.lxm.netty.server.entitytype.EntityTypeRegistry <br/>
 * Function: FUNCTION <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class EntityTypeRegistry {
    
    /**
     * 注册{@link EntityTypeEnum}中所有的类型
     */
    @SuppressWarnings("unchecked")
    public static void registryEntityType(){
        for(EntityTypeEnum enum1: EntityTypeEnum.values()){
            EntityDecoderRegistry.addDecoder(enum1.getType(), enum1.getCoder());
            EntityEncoderRegistry.addEncoder(enum1.getType(), enum1.getCoder());
            RequestProcessorRegistry.addProcessor(enum1.getType(), enum1.getProcessor());
        }
    }

}
