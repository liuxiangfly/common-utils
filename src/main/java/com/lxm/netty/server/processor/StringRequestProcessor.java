package com.lxm.netty.server.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxm.netty.constants.ResponseStatus;
import com.lxm.netty.entity.RequestEntity;
import com.lxm.netty.entity.ResponseEntity;

/**
 * ClassName: com.lxm.netty.server.processor.StringRequestProcessor <br/>
 * Function: 业务处理器，处理实体类型为String的业务 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class StringRequestProcessor implements RequestProcessor<String, String>{
    

    private static final Logger logger = LoggerFactory.getLogger(StringRequestProcessor.class);
    

    /* (non-Javadoc)
     * @see com.lxm.netty.server.processor.RequestProcessor#processRequest(com.lxm.netty.entity.RequestEntity)
     */
    @Override
    public ResponseEntity<String> processRequest(RequestEntity<String> request) {
        logger.info("request, id:{}, type:{}, data:{}", request.getId(), request.getType(), request.getData());
        return new ResponseEntity<>(request.getId(), request.getType(), ResponseStatus.OK, request.getData() + "....response");
    }

}
