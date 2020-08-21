package com.lxm.netty.client;

import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxm.netty.client.codec.NettyRequestEncoder;
import com.lxm.netty.client.codec.NettyResponseDecoder;
import com.lxm.netty.client.config.ClientConfig;
import com.lxm.netty.client.exception.ClientException;
import com.lxm.netty.client.handler.ClientHandler;
import com.lxm.netty.client.handler.ClientPromiseHolder;
import com.lxm.netty.entity.RequestEntity;
import com.lxm.netty.entity.ResponseEntity;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.concurrent.GenericFutureListener;


public class NettyClient {
    

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    public static final int RECONNECT_DELAY_MS = 2000;

    private final String host;
    private final int port;

    private Channel channel;
    private NioEventLoopGroup eventLoopGroup;
    private ClientHandler clientHandler;

    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private final AtomicInteger currentState = new AtomicInteger(ClientConstants.CLIENT_STATUS_OFF);
    private final AtomicInteger failConnectedTime = new AtomicInteger(0);

    private final AtomicBoolean shouldRetry = new AtomicBoolean(true);

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private Bootstrap initClientBootstrap() {
        Bootstrap b = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        b.group(eventLoopGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ClientConfig.getConnectTimeout())
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    clientHandler = new ClientHandler(currentState, disconnectCallback);

                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2));
                    pipeline.addLast(new NettyResponseDecoder());
                    pipeline.addLast(new LengthFieldPrepender(2));
                    pipeline.addLast(new NettyRequestEncoder());
                    pipeline.addLast(clientHandler);
                }
            });

        return b;
    }

    private void connect(Bootstrap b) {
        if (currentState.compareAndSet(ClientConstants.CLIENT_STATUS_OFF, ClientConstants.CLIENT_STATUS_PENDING)) {
            b.connect(host, port)
                .addListener(new GenericFutureListener<ChannelFuture>() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.cause() != null) {
                        logger.warn("Could not connect to <{}:{}> after {} times.", host, port, failConnectedTime.get(), future.cause());
                        failConnectedTime.incrementAndGet();
                        channel = null;
                    } else {
                        failConnectedTime.set(0);
                        channel = future.channel();
                        logger.info("Successfully connect to server <{}:{}>", host, port);
                    }
                }
            });
        }
    }

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            if (!shouldRetry.get()) {
                return;
            }
            SCHEDULER.schedule(new Runnable() {
                @Override
                public void run() {
                    if (shouldRetry.get()) {
                        logger.info("Reconnecting to server <{}:{}", host, port);
                        try {
                            startInternal();
                        } catch (Exception e) {
                            logger.warn("Failed to reconnect to server, ", e);
                        }
                    }
                }
            }, RECONNECT_DELAY_MS * (failConnectedTime.get() + 1), TimeUnit.MILLISECONDS);
            cleanUp();
        }
    };

    public void start() throws Exception {
        shouldRetry.set(true);
        startInternal();
    }

    private void startInternal() {
        connect(initClientBootstrap());
    }

    private void cleanUp() {
        if (channel != null) {
            channel.close();
            channel = null;
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void stop() throws Exception {
        // Stop retrying for connection.
        shouldRetry.set(false);

        while (currentState.get() == ClientConstants.CLIENT_STATUS_PENDING) {
            try {
                Thread.sleep(200);
            } catch (Exception ex) {
                // Ignore.
            }
        }

        cleanUp();
        failConnectedTime.set(0);

        logger.info("Netty client stopped");
    }

    private boolean validRequest(RequestEntity request) {
        return request != null && request.getType() >= 0;
    }

    public boolean isReady() {
        return channel != null && clientHandler != null && clientHandler.hasStarted();
    }

    public ResponseEntity sendRequest(RequestEntity request) throws Exception {
        if (!isReady()) {
            throw new ClientException("client not ready");
        }
        if (!validRequest(request)) {
            throw new ClientException("bad request");
        }
        int xid = getCurrentId();
        try {
            request.setId(xid);

            channel.writeAndFlush(request);

            ChannelPromise promise = channel.newPromise();
            ClientPromiseHolder.putPromise(xid, promise);

            if (!promise.await(ClientConfig.getRequestTimeout())) {
                throw new ClientException("request time out");
            }

            SimpleEntry<ChannelPromise, ResponseEntity> entry = ClientPromiseHolder.getEntry(xid);
            if (entry == null || entry.getValue() == null) {
                throw new ClientException("unexpected status");
            }
            return entry.getValue();
        } finally {
            ClientPromiseHolder.remove(xid);
        }
    }

    private int getCurrentId() {
        if (idGenerator.get() > MAX_ID) {
            idGenerator.set(0);
        }
        return idGenerator.incrementAndGet();
    }

    private static final int MAX_ID = 999_999_999;
}
