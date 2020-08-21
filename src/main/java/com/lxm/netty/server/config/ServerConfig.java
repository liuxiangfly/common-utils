package com.lxm.netty.server.config;

public class ServerConfig {
    
    private ServerConfig(){
        // ServerConfig
    }
    
    /**
     * 连接空闲时间，超过该时间将关闭释放
     */
    private static int idleSeconds = 600;
    
    /**
     * 处理报文最大长度（字节）
     */
    private static int maxFrameLength = 1024;

    public static int getIdleSeconds() {
        return idleSeconds;
    }

    public static void setIdleSeconds(int idleSeconds) {
        ServerConfig.idleSeconds = idleSeconds;
    }

    public static int getMaxFrameLength() {
        return maxFrameLength;
    }

    public static void setMaxFrameLength(int maxFrameLength) {
        ServerConfig.maxFrameLength = maxFrameLength;
    }

}
