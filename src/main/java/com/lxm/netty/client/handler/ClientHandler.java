package com.lxm.netty.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxm.netty.client.ClientConstants;
import com.lxm.netty.entity.ResponseEntity;


public class ClientHandler extends ChannelInboundHandlerAdapter {
    

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    

    private final AtomicInteger currentState;
    private final Runnable disconnectCallback;

    public ClientHandler(AtomicInteger currentState, Runnable disconnectCallback) {
        this.currentState = currentState;
        this.disconnectCallback = disconnectCallback;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        currentState.set(ClientConstants.CLIENT_STATUS_STARTED);
        logger.info("Client handler active, remote address: " + getRemoteAddress(ctx));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ResponseEntity) {
            ResponseEntity<?> response = (ResponseEntity) msg;

            ClientPromiseHolder.completePromise(response.getId(), response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Client exception caught:", cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Client handler inactive, remote address: " + getRemoteAddress(ctx));
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("Client channel unregistered, remote address: " + getRemoteAddress(ctx));
        currentState.set(ClientConstants.CLIENT_STATUS_OFF);
        disconnectCallback.run();
    }

    private String getRemoteAddress(ChannelHandlerContext ctx) {
        if (ctx.channel().remoteAddress() == null) {
            return null;
        }
        InetSocketAddress inetAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return inetAddress.getAddress().getHostAddress() + ":" + inetAddress.getPort();
    }

    public int getCurrentState() {
        return currentState.get();
    }

    public boolean hasStarted() {
        return getCurrentState() == ClientConstants.CLIENT_STATUS_STARTED;
    }
}
