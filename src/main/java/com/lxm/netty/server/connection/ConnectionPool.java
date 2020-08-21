package com.lxm.netty.server.connection;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;

public class ConnectionPool {
    
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    

    private static final ScheduledExecutorService TIMER          = Executors.newScheduledThreadPool(2);

    /**
     * Format: ("ip:port", connection)
     */
    private final Map<String, Connection>         CONNECTION_MAP = new ConcurrentHashMap<>();

    /**
     * Periodic scan task.
     */
    private ScheduledFuture                       scanTaskFuture = null;

    public void createConnection(Channel channel) {
        if (channel != null) {
            Connection connection = new NettyConnection(channel, this);

            String connKey = getConnectionKey(channel);
            CONNECTION_MAP.put(connKey, connection);
        }
    }

    /**
     * Start the scan task for long-idle connections.
     */
    private synchronized void startScan() {
        if (scanTaskFuture == null || scanTaskFuture.isCancelled() || scanTaskFuture.isDone()) {
            scanTaskFuture = TIMER.scheduleAtFixedRate(new ScanIdleConnectionTask(this), 10, 30, TimeUnit.SECONDS);
        }
    }

    /**
     * Format to "ip:port".
     *
     * @param channel channel
     * @return formatted key
     */
    private String getConnectionKey(Channel channel) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        String remoteIp = socketAddress.getAddress().getHostAddress();
        int remotePort = socketAddress.getPort();
        return remoteIp + ":" + remotePort;
    }

    private String getConnectionKey(String ip, int port) {
        return ip + ":" + port;
    }

    public void refreshLastReadTime(Channel channel) {
        if (channel != null) {
            String connKey = getConnectionKey(channel);
            Connection connection = CONNECTION_MAP.get(connKey);
            if (connection != null) {
                connection.refreshLastReadTime(System.currentTimeMillis());
            }
        }
    }

    public Connection getConnection(String remoteIp, int remotePort) {
        String connKey = getConnectionKey(remoteIp, remotePort);
        return CONNECTION_MAP.get(connKey);
    }

    public void remove(Channel channel) {
        String connKey = getConnectionKey(channel);
        CONNECTION_MAP.remove(connKey);
    }

    public List<Connection> listAllConnection() {
        return new ArrayList<>(CONNECTION_MAP.values());
    }

    public int count() {
        return CONNECTION_MAP.size();
    }

    public void clear() {
        CONNECTION_MAP.clear();
    }

    public void shutdownAll() throws Exception {
        for (Connection c : CONNECTION_MAP.values()) {
            c.close();
        }
    }

    public void refreshIdleTask() {
        if (scanTaskFuture == null || scanTaskFuture.cancel(false)) {
            startScan();
        } else {
            logger.info("The result of canceling scanTask is error.");
        }
    }
}
