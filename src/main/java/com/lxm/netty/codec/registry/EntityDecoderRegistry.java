package com.lxm.netty.codec.registry;

import java.util.HashMap;
import java.util.Map;

import com.lxm.netty.codec.coder.EntityDecoder;

/**
 * 
 * ClassName: com.lxm.netty.codec.registry.EntityDecoderRegistry <br/>
 * Function: 实体类解析器注册器 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public final class EntityDecoderRegistry {

    private static final Map<Integer, EntityDecoder<Object>> DECODER_MAP = new HashMap<>();

    public static <T> boolean addDecoder(int type, EntityDecoder<T> decoder) {
        if (DECODER_MAP.containsKey(type)) {
            return false;
        }
        DECODER_MAP.put(type, (EntityDecoder<Object>) decoder);
        return true;
    }

    public static EntityDecoder<Object> getDecoder(int type) {
        return DECODER_MAP.get(type);
    }

    public static boolean removeDecoder(int type) {
        return DECODER_MAP.remove(type) != null;
    }
}
