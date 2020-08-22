package com.lxm.netty.server.processor;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * ClassName: com.lxm.netty.server.processor.RequestProcessorRegistry <br/>
 * Function: 请求处理器注册器<br/>
 * Date: 2020年8月22日 下午3:29:57 <br/>
 * @author liuxiangming <br/>
 */
public final class RequestProcessorRegistry {

    private static final Map<Integer, RequestProcessor> PROCESSOR_MAP = new HashMap<>();

    public static RequestProcessor getProcessor(int type) {
        return PROCESSOR_MAP.get(type);
    }

    public static boolean addProcessor(int type, RequestProcessor processor) {
        if (PROCESSOR_MAP.containsKey(type)) {
            return false;
        }
        PROCESSOR_MAP.put(type, processor);
        return true;
    }
    
    public static boolean removeProcessor(int type) {
        return PROCESSOR_MAP.remove(type) != null;
    }


    private RequestProcessorRegistry() {}
}
