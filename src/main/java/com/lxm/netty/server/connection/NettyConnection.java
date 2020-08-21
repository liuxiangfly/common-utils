package com.lxm.netty.server.connection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.netty.channel.Channel;

public class NettyConnection implements Connection {

    private String         remoteIp;
    private int            remotePort;
    private Channel        channel;

    private long           lastReadTime;

    private ConnectionPool pool;

    public NettyConnection(Channel channel, ConnectionPool pool) {
        this.channel = channel;
        this.pool = pool;
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        this.remoteIp = socketAddress.getAddress().getHostAddress();
        this.remotePort = socketAddress.getPort();
        this.lastReadTime = System.currentTimeMillis();
    }

    @Override
    public SocketAddress getLocalAddress() {
        return channel.localAddress();
    }

    @Override
    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public String getRemoteIP() {
        return remoteIp;
    }

    @Override
    public void refreshLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    @Override
    public long getLastReadTime() {
        return lastReadTime;
    }

    @Override
    public String getConnectionKey() {
        return remoteIp + ":" + remotePort;
    }

    @Override
    public void close() {
        // Remove from connection pool.
        pool.remove(channel);
        // Close the connection.
        if (channel != null && channel.isActive()) {
            channel.close();
        }
    }
}
