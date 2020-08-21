package com.lxm.netty.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxm.netty.constants.ResponseStatus;
import com.lxm.netty.entity.RequestEntity;
import com.lxm.netty.entity.ResponseEntity;
import com.lxm.netty.server.connection.ConnectionPool;
import com.lxm.netty.server.processor.RequestProcessor;
import com.lxm.netty.server.processor.RequestProcessorProvider;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    

    private final ConnectionPool globalConnectionPool;

    public ServerHandler(ConnectionPool connectionPool) {
        this.globalConnectionPool = connectionPool;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        globalConnectionPool.createConnection(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        globalConnectionPool.remove(ctx.channel());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        globalConnectionPool.refreshLastReadTime(ctx.channel());
        if (msg instanceof RequestEntity) {
            RequestEntity request = (RequestEntity)msg;
            // 根据请求类型选择处理器处理请求（防止netty事件处理线程（每个socketChannel分配一个线程处理，客户端使用一个channel连续发送请求时，则在一个线程中串行处理）阻塞，这里可采用线程池处理）
            RequestProcessor<?, ?> processor = RequestProcessorProvider.getProcessor(request.getType());
            if (processor == null) {
                logger.warn("No processor for request type: {}", request.getType());
                writeBadResponse(ctx, request);
            } else {
                ResponseEntity<?> response = processor.processRequest(request);
                writeResponse(ctx, response);
            }
        }
    }

    private void writeBadResponse(ChannelHandlerContext ctx, RequestEntity request) {
        ResponseEntity<?> response = new ResponseEntity<>(request.getId(), request.getType(),
            ResponseStatus.BAD, null);
        writeResponse(ctx, response);
    }

    private void writeResponse(ChannelHandlerContext ctx, ResponseEntity response) {
        ctx.writeAndFlush(response);
    }
    
}
