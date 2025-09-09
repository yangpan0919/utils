package com.example.tcpproxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TCP服务端配置类
 * 用于配置TCP服务端的端口、接收和发送的固定字节数据
 */
@Data
@Component
@ConfigurationProperties(prefix = "tcp.server")
public class TcpServerConfig {
    
    /**
     * TCP服务端端口
     */
    private int port = 8080;
    
    /**
     * 是否启用TCP服务端
     */
    private boolean enabled = true;
    
    /**
     * 接收的固定字节数据配置
     */
    private List<ByteDataConfig> receiveData;
    
    /**
     * 发送的固定字节数据配置
     */
    private List<ByteDataConfig> sendData;
    
    /**
     * 字节数据配置
     */
    @Data
    public static class ByteDataConfig {
        /**
         * 数据名称/描述
         */
        private String name;
        
        /**
         * 十六进制字符串表示的字节数据，如 "01 02 03 04"
         */
        private String hexData;
        
        /**
         * 是否启用此配置
         */
        private boolean enabled = true;
    }
}
