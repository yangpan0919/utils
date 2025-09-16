package com.example.tcpproxy.server;

import com.example.tcpproxy.config.SerialServerConfig;
import com.example.tcpproxy.handler.SerialMessageHandler;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 串口服务类
 * 用于模拟串口通讯的服务端，支持可配置的固定字节数据收发
 */
@Slf4j
@Component
public class SerialServer {
    
    @Autowired
    private SerialServerConfig serialServerConfig;
    
    @Autowired
    private SerialMessageHandler serialMessageHandler;
    
    private SerialPort serialPort;
    private ExecutorService executorService;
    private volatile boolean running = false;
    
    @PostConstruct
    public void start() {
        if (!serialServerConfig.isEnabled()) {
            log.info("串口服务未启用");
            return;
        }
        
        new Thread(() -> {
            try {
                startSerialServer();
            } catch (Exception e) {
                log.error("串口服务启动失败", e);
            }
        }, "SerialServer-Thread").start();
    }
    
    private void startSerialServer() {
        try {
            // 获取串口
            serialPort = SerialPort.getCommPort(serialServerConfig.getPortName());
            
            if (serialPort == null) {
                log.error("无法找到串口: {}", serialServerConfig.getPortName());
                return;
            }
            
            // 配置串口参数
            serialPort.setBaudRate(serialServerConfig.getBaudRate());
            serialPort.setNumDataBits(serialServerConfig.getDataBits());
            serialPort.setNumStopBits(serialServerConfig.getStopBits());
            serialPort.setParity(serialServerConfig.getParity());
            serialPort.setFlowControl(serialServerConfig.getFlowControl());
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 
                    serialServerConfig.getReadTimeout(), 0);
            
            // 打开串口
            if (!serialPort.openPort()) {
                log.error("无法打开串口: {}", serialServerConfig.getPortName());
                return;
            }
            
            running = true;
            log.info("串口服务启动成功，串口: {} 波特率: {}", 
                    serialServerConfig.getPortName(), serialServerConfig.getBaudRate());
            
            // 启动消息处理线程
            executorService = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "SerialMessageHandler-Thread");
                t.setDaemon(true);
                return t;
            });
            
            executorService.submit(this::processSerialMessages);
            
        } catch (Exception e) {
            log.error("串口服务启动异常", e);
        }
    }
    
    /**
     * 处理串口消息
     */
    private void processSerialMessages() {
        byte[] buffer = new byte[1024];
        
        while (running && serialPort != null && serialPort.isOpen()) {
            try {
                // 读取串口数据
                int bytesRead = serialPort.readBytes(buffer, buffer.length);
                
                if (bytesRead > 0) {
                    byte[] receivedData = new byte[bytesRead];
                    System.arraycopy(buffer, 0, receivedData, 0, bytesRead);
                    
                    log.info("接收到串口数据 [{}]: {}", serialServerConfig.getPortName(), 
                            bytesToHexString(receivedData));
                    
                    // 处理接收到的消息
                    byte[] responseData = serialMessageHandler.handleMessage(receivedData);
                    
                    if (responseData != null && responseData.length > 0) {
                        // 发送回复数据
                        int bytesWritten = serialPort.writeBytes(responseData, responseData.length);
                        if (bytesWritten > 0) {
                            log.info("发送串口回复数据 [{}]: {}", serialServerConfig.getPortName(), 
                                    bytesToHexString(responseData));
                        } else {
                            log.warn("串口数据发送失败");
                        }
                    }
                }
                
                // 短暂休眠避免CPU占用过高
                Thread.sleep(10);
                
            } catch (InterruptedException e) {
                log.info("串口消息处理线程被中断");
                break;
            } catch (Exception e) {
                log.error("处理串口消息时发生异常", e);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    break;
                }
            }
        }
        
        log.info("串口消息处理线程结束");
    }
    
    @PreDestroy
    public void shutdown() {
        running = false;
        
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
        
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            log.info("串口已关闭: {}", serialServerConfig.getPortName());
        }
    }
    
    /**
     * 检查串口服务是否运行中
     */
    public boolean isRunning() {
        return running && serialPort != null && serialPort.isOpen();
    }
    
    /**
     * 获取串口名称
     */
    public String getPortName() {
        return serialServerConfig.getPortName();
    }
    
    /**
     * 将字节数组转换为十六进制字符串
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
