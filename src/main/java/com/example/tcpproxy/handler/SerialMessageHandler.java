package com.example.tcpproxy.handler;

import com.example.tcpproxy.config.SerialServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 串口消息处理器
 * 处理接收到的串口数据并返回固定字节数据
 */
@Slf4j
@Component
public class SerialMessageHandler {
    
    @Autowired
    private SerialServerConfig serialServerConfig;
    
    /**
     * 处理接收到的串口消息
     * @param receivedData 接收到的数据
     * @return 回复的数据，如果不需要回复则返回null
     */
    public byte[] handleMessage(byte[] receivedData) {
        if (receivedData == null || receivedData.length == 0) {
            return null;
        }
        
        // 查找匹配的接收数据配置
        SerialServerConfig.ByteDataConfig matchedConfig = findMatchingReceiveConfig(receivedData);
        
        if (matchedConfig != null) {
            log.info("匹配到接收配置: {}", matchedConfig.getName());
            
            // 查找对应的发送配置
            SerialServerConfig.ByteDataConfig sendConfig = findSendConfig(matchedConfig.getName());
            
            if (sendConfig != null && sendConfig.isEnabled()) {
                byte[] sendData = hexStringToBytes(sendConfig.getHexData());
                if (sendData != null && sendData.length > 0) {
                    log.info("准备发送回复数据: {}", bytesToHexString(sendData));
                    return sendData;
                } else {
                    log.warn("发送配置数据为空或格式错误: {}", sendConfig.getName());
                }
            } else {
                log.warn("未找到对应的发送配置: {}", matchedConfig.getName());
            }
        } else {
            log.warn("未找到匹配的接收配置，接收数据: {}", bytesToHexString(receivedData));
        }
        
        return null;
    }
    
    /**
     * 查找匹配的接收数据配置
     */
    private SerialServerConfig.ByteDataConfig findMatchingReceiveConfig(byte[] receivedData) {
        if (serialServerConfig.getReceiveData() == null) {
            return null;
        }
        
        for (SerialServerConfig.ByteDataConfig config : serialServerConfig.getReceiveData()) {
            if (!config.isEnabled()) {
                continue;
            }
            
            byte[] configData = hexStringToBytes(config.getHexData());
            if (configData != null && Arrays.equals(receivedData, configData)) {
                return config;
            }
        }
        
        return null;
    }
    
    /**
     * 根据名称查找发送配置
     */
    private SerialServerConfig.ByteDataConfig findSendConfig(String name) {
        if (serialServerConfig.getSendData() == null) {
            return null;
        }
        
        for (SerialServerConfig.ByteDataConfig config : serialServerConfig.getSendData()) {
            if (config.getName() != null && config.getName().equals(name)) {
                return config;
            }
        }
        
        return null;
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
    
    /**
     * 将十六进制字符串转换为字节数组
     */
    private byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 移除空格和分隔符
            String cleanHex = hexString.replaceAll("\\s+", "").replaceAll("[^0-9A-Fa-f]", "");
            
            if (cleanHex.length() % 2 != 0) {
                log.warn("十六进制字符串长度不是偶数: {}", hexString);
                return null;
            }
            
            byte[] bytes = new byte[cleanHex.length() / 2];
            for (int i = 0; i < bytes.length; i++) {
                int index = i * 2;
                bytes[i] = (byte) Integer.parseInt(cleanHex.substring(index, index + 2), 16);
            }
            
            return bytes;
        } catch (NumberFormatException e) {
            log.warn("十六进制字符串格式错误: {}", hexString, e);
            return null;
        }
    }
}
