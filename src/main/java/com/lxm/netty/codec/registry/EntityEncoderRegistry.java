package com.lxm.netty.codec.registry;

import java.util.HashMap;
import java.util.Map;

import com.lxm.netty.codec.coder.EntityEncoder;

/**
 * 
 * ClassName: com.lxm.netty.codec.registry.EntityEncoderRegistry <br/>
 * Function: 实体类编码器注册器 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public final class EntityEncoderRegistry {

    private static final Map<Integer, EntityEncoder<Object>> WRITER_MAP = new HashMap<>();

    public static <T> boolean addEncoder(int type, EntityEncoder<T> writer) {
        if (WRITER_MAP.containsKey(type)) {
            return false;
        }
        WRITER_MAP.put(type, (EntityEncoder<Object>)writer);
        return true;
    }

    public static EntityEncoder<Object> getEncoder(int type) {
        return WRITER_MAP.get(type);
    }

    public static boolean remove(int type) {
        return WRITER_MAP.remove(type) != null;
    }
}
