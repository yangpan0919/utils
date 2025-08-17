package com.example.tcpproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfig {
    
    // 本地服务器监听端口
    private int localPort = 8080;
    
    // 远程服务器地址
    private String remoteHost = "localhost";
    
    // 远程服务器端口
    private int remotePort = 9090;
} 