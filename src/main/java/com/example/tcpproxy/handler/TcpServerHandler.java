package com.example.tcpproxy.handler;

import com.example.tcpproxy.config.TcpServerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * TCP服务端处理器
 * 处理接收到的TCP数据并回复固定字节数据
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    
    @Autowired
    private TcpServerConfig tcpServerConfig;
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端连接: {}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开连接: {}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        try {
            // 读取接收到的数据
            byte[] receivedData = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(receivedData);
            
            log.info("接收到数据 [{}]: {}", ctx.channel().remoteAddress(), 
                    bytesToHexString(receivedData));
            
            // 查找匹配的接收数据配置
            TcpServerConfig.ByteDataConfig matchedConfig = findMatchingReceiveConfig(receivedData);
            
            if (matchedConfig != null) {
                log.info("匹配到接收配置: {}", matchedConfig.getName());
                
                // 查找对应的发送配置
                TcpServerConfig.ByteDataConfig sendConfig = findSendConfig(matchedConfig.getName());
                
                if (sendConfig != null && sendConfig.isEnabled()) {
                    byte[] sendData = hexStringToBytes(sendConfig.getHexData());
                    if (sendData != null && sendData.length > 0) {
                        // 发送回复数据
                        ByteBuf responseBuf = ctx.alloc().buffer(sendData.length);
                        responseBuf.writeBytes(sendData);
                        ctx.writeAndFlush(responseBuf);
                        
                        log.info("发送回复数据 [{}]: {}", ctx.channel().remoteAddress(), 
                                bytesToHexString(sendData));
                    } else {
                        log.warn("发送配置数据为空或格式错误: {}", sendConfig.getName());
                    }
                } else {
                    log.warn("未找到对应的发送配置: {}", matchedConfig.getName());
                }
            } else {
                log.warn("未找到匹配的接收配置，接收数据: {}", bytesToHexString(receivedData));
            }
            
        } finally {
            byteBuf.release();
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("TCP连接异常: {}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
    
    /**
     * 查找匹配的接收数据配置
     */
    private TcpServerConfig.ByteDataConfig findMatchingReceiveConfig(byte[] receivedData) {
        if (tcpServerConfig.getReceiveData() == null) {
            return null;
        }
        
        for (TcpServerConfig.ByteDataConfig config : tcpServerConfig.getReceiveData()) {
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
    private TcpServerConfig.ByteDataConfig findSendConfig(String name) {
        if (tcpServerConfig.getSendData() == null) {
            return null;
        }
        
        for (TcpServerConfig.ByteDataConfig config : tcpServerConfig.getSendData()) {
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
