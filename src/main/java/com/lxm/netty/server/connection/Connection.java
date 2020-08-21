package com.lxm.netty.server.connection;

import java.net.SocketAddress;

public interface Connection extends AutoCloseable {

    SocketAddress getLocalAddress();

    int getRemotePort();

    String getRemoteIP();

    void refreshLastReadTime(long lastReadTime);

    long getLastReadTime();

    String getConnectionKey();
}
