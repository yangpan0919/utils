package com.example.tcpproxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 串口服务配置类
 * 用于配置串口服务的端口参数、接收和发送的固定字节数据
 */
@Data
@Component
@ConfigurationProperties(prefix = "serial.server")
public class SerialServerConfig {
    
    /**
     * 串口名称，如 "COM1", "COM3" 等
     */
    private String portName = "COM1";
    
    /**
     * 波特率
     */
    private int baudRate = 9600;
    
    /**
     * 数据位
     */
    private int dataBits = 8;
    
    /**
     * 停止位
     */
    private int stopBits = 1;
    
    /**
     * 校验位 (0=无校验, 1=奇校验, 2=偶校验)
     */
    private int parity = 0;
    
    /**
     * 流控制 (0=无流控制, 1=RTS/CTS, 2=XON/XOFF)
     */
    private int flowControl = 0;
    
    /**
     * 是否启用串口服务
     */
    private boolean enabled = true;
    
    /**
     * 接收超时时间（毫秒）
     */
    private int readTimeout = 1000;
    
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
