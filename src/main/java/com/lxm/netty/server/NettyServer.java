package com.lxm.netty.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxm.netty.server.codec.NettyRequestDecoder;
import com.lxm.netty.server.codec.NettyResponseEncoder;
import com.lxm.netty.server.config.ServerConfig;
import com.lxm.netty.server.connection.Connection;
import com.lxm.netty.server.connection.ConnectionPool;
import com.lxm.netty.server.handler.ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.SystemPropertyUtil;

/**
 * 
 * ClassName: com.lxm.netty.server.NettyServer <br/>
 * Function: netty服务器 <br/>
 * Date: 2020年8月19日 <br/>
 * @author liuxiangming
 */
public class NettyServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    

    private static final int     DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
                                                                    "io.netty.eventLoopThreads", Runtime.getRuntime()
                                                                            .availableProcessors() * 2));
    private static final int     MAX_RETRY_TIMES            = 3;
    private static final int     RETRY_SLEEP_MS             = 2000;

    private final int            port;

    private NioEventLoopGroup    bossGroup;
    private NioEventLoopGroup    workerGroup;

    private final ConnectionPool connectionPool             = new ConnectionPool();

    private final AtomicInteger  currentState               = new AtomicInteger(ServerConstants.SERVER_STATUS_OFF);
    private final AtomicInteger  failedTimes                = new AtomicInteger(0);

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() {
        if (!currentState.compareAndSet(ServerConstants.SERVER_STATUS_OFF, ServerConstants.SERVER_STATUS_STARTING)) {
            return;
        }
        connectionPool.refreshIdleTask();
        ServerBootstrap b = new ServerBootstrap();
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(DEFAULT_EVENT_LOOP_THREADS);
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new LengthFieldBasedFrameDecoder(ServerConfig.getMaxFrameLength(), 0, 2, 0, 2));
                        p.addLast(new NettyRequestDecoder());
                        p.addLast(new LengthFieldPrepender(2));
                        p.addLast(new NettyResponseEncoder());
                        p.addLast(new ServerHandler(connectionPool));
                    }
                }).childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
                .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000).childOption(ChannelOption.SO_TIMEOUT, 10)
                .childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_RCVBUF, 32 * 1024);
        b.bind(port).addListener(new GenericFutureListener<ChannelFuture>() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.cause() != null) {
                    logger.info("Token server start failed (port={}), failedTimes: {}, {}", port, failedTimes.get(), future.cause());
                    currentState.compareAndSet(ServerConstants.SERVER_STATUS_STARTING, ServerConstants.SERVER_STATUS_OFF);
                    int failCount = failedTimes.incrementAndGet();
                    if (failCount > MAX_RETRY_TIMES) {
                        return;
                    }

                    try {
                        Thread.sleep(failCount * RETRY_SLEEP_MS);
                        start();
                    } catch (Exception e) {
                        logger.info("Failed to start token server when retrying, exception:", e);
                    }
                } else {
                    logger.info("Netty server started success at port: ", port);
                    currentState.compareAndSet(ServerConstants.SERVER_STATUS_STARTING, ServerConstants.SERVER_STATUS_STARTED);
                }
            }
        });
    }

    public void stop() {
        // If still initializing, wait for ready.
        while (currentState.get() == ServerConstants.SERVER_STATUS_STARTING) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Ignore.
            }
        }

        if (currentState.compareAndSet(ServerConstants.SERVER_STATUS_STARTED, ServerConstants.SERVER_STATUS_OFF)) {
            try {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                connectionPool.shutdownAll();

                failedTimes.set(0);

                logger.info("Netty server stopped");
            } catch (Exception ex) {
                logger.warn("Failed to stop netty server (port={}), exception:", port, ex);
            }
        }
    }

    public void closeConnection(String clientIp, int clientPort) throws Exception {
        Connection connection = connectionPool.getConnection(clientIp, clientPort);
        connection.close();
    }

    public void closeAll() throws Exception {
        List<Connection> connections = connectionPool.listAllConnection();
        for (Connection connection : connections) {
            connection.close();
        }
    }

    public List<String> listAllClient() {
        List<String> clients = new ArrayList<>();
        List<Connection> connections = connectionPool.listAllConnection();
        for (Connection conn : connections) {
            clients.add(conn.getConnectionKey());
        }
        return clients;
    }

    public int getCurrentState() {
        return currentState.get();
    }

    public int clientCount() {
        return connectionPool.count();
    }

}
