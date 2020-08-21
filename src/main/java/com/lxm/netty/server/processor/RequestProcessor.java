package com.lxm.netty.server.processor;

import com.lxm.netty.entity.RequestEntity;
import com.lxm.netty.entity.ResponseEntity;

/**
 * Interface of request processor.
 *
 * @param <T> type of request body
 * @param <R> type of response body
 * 
 */
public interface RequestProcessor<T, R> {


    ResponseEntity<R> processRequest(RequestEntity<T> request);
}
