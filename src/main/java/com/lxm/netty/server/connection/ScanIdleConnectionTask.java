package com.lxm.netty.server.connection;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxm.netty.server.config.ServerConfig;

public class ScanIdleConnectionTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ScanIdleConnectionTask.class);
    
    private final ConnectionPool connectionPool;

    public ScanIdleConnectionTask(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void run() {
        try {
            int idleSeconds = ServerConfig.getIdleSeconds();
            long idleTimeMillis = idleSeconds * 1000;
            long now = System.currentTimeMillis();
            List<Connection> connections = connectionPool.listAllConnection();
            for (Connection conn : connections) {
                if ((now - conn.getLastReadTime()) > idleTimeMillis) {
                    logger.info("The connection <{}:{}> has been idle for <{}>s. ", conn.getRemoteIP(), conn.getRemotePort(), idleSeconds);
                    conn.close();
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to clean-up idle tasks, exception:", e);
        }
    }
}
