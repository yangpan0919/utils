package com.example.tcpproxy;

import com.example.tcpproxy.server.SerialServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 串口模拟服务应用主启动类
 * 启动串口服务端，用于模拟串口通讯测试
 */
@Slf4j
@SpringBootApplication
public class TcpProxyApplication implements CommandLineRunner {
    
    @Autowired
    private SerialServer serialServer;
    
    public static void main(String[] args) {
        SpringApplication.run(TcpProxyApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("串口模拟服务应用启动完成");
        log.info("串口服务状态: {}", serialServer.isRunning() ? "运行中" : "未运行");
        log.info("串口名称: {}", serialServer.getPortName());
        
        // 保持应用运行
        Thread.currentThread().join();
    }
}
