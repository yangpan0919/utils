package com.example.serialproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfig {
    
    // 源串口配置
    private SerialPortConfig sourcePort = new SerialPortConfig();
    
    // 目标串口配置
    private SerialPortConfig targetPort = new SerialPortConfig();
    
    @Data
    public static class SerialPortConfig {
        // 串口名称，如 COM1, COM2 等
        private String portName = "COM1";
        
        // 波特率
        private int baudRate = 9600;
        
        // 数据位 (5, 6, 7, 8)
        private int dataBits = 8;
        
        // 停止位 (1, 2)
        private int stopBits = 1;
        
        // 校验位 (0=无校验, 1=奇校验, 2=偶校验)
        private int parity = 0;
        
        // 流控制 (0=无流控, 1=硬件流控, 2=软件流控)
        private int flowControl = 0;
    }
}
