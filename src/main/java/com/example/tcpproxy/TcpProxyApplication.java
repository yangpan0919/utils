package com.example.tcpproxy;

import com.example.tcpproxy.server.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TCP代理应用主启动类
 * 启动基于Netty的TCP服务端，用于模拟TCP通讯测试
 */
@Slf4j
@SpringBootApplication
public class TcpProxyApplication implements CommandLineRunner {
    
    @Autowired
    private TcpServer tcpServer;
    
    public static void main(String[] args) {
        SpringApplication.run(TcpProxyApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("TCP代理应用启动完成");
        log.info("TCP服务端状态: {}", tcpServer.isRunning() ? "运行中" : "未运行");
        log.info("TCP服务端端口: {}", tcpServer.getPort());
        
        // 保持应用运行
        Thread.currentThread().join();
    }
}
