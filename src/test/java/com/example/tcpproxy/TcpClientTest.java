package com.example.tcpproxy;

import java.io.*;
import java.net.Socket;

/**
 * TCP客户端测试类
 * 用于测试TCP服务端的功能
 */
public class TcpClientTest {
    
    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 12290; // 使用配置的新端口
        
        try (Socket socket = new Socket(serverHost, serverPort)) {
            System.out.println("连接到TCP服务端: " + serverHost + ":" + serverPort);
            
            // 测试数据包1
            System.out.println("发送数据包1: 30 31 57 52 44 44 30 31 31 31 31 2C 31 0D 0A");
            sendHexData(socket, "30 31 57 52 44 44 30 31 31 31 31 2C 31 0D 0A");
            Thread.sleep(1000);
            
            // 测试数据包2
            System.out.println("发送数据包2: 30 31 57 52 44 44 30 31 31 31 32 2C 31 30 0D 0A");
            sendHexData(socket, "30 31 57 52 44 44 30 31 31 31 32 2C 31 30 0D 0A");
            Thread.sleep(1000);
            
            // 测试数据包3
            System.out.println("发送数据包3: 30 31 57 52 44 44 30 31 32 31 33 2C 31 0D 0A");
            sendHexData(socket, "30 31 57 52 44 44 30 31 32 31 33 2C 31 0D 0A");
            Thread.sleep(1000);
            
            // 测试数据包4
            System.out.println("发送数据包4: 30 31 57 52 44 44 30 31 32 31 34 2C 31 0D 0A");
            sendHexData(socket, "30 31 57 52 44 44 30 31 32 31 34 2C 31 0D 0A");
            Thread.sleep(1000);
            
            // 测试未知数据
            System.out.println("发送未知数据: FF EE DD CC");
            sendHexData(socket, "FF EE DD CC");
            Thread.sleep(1000);
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 发送十六进制数据
     */
    private static void sendHexData(Socket socket, String hexString) throws IOException {
        byte[] data = hexStringToBytes(hexString);
        if (data != null) {
            OutputStream out = socket.getOutputStream();
            out.write(data);
            out.flush();
            System.out.println("已发送: " + bytesToHexString(data));
            
            // 尝试读取回复
            try {
                InputStream in = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
                if (bytesRead > 0) {
                    byte[] response = new byte[bytesRead];
                    System.arraycopy(buffer, 0, response, 0, bytesRead);
                    System.out.println("收到回复: " + bytesToHexString(response));
                }
            } catch (IOException e) {
                System.out.println("未收到回复或连接已关闭");
            }
        }
    }
    
    /**
     * 将十六进制字符串转换为字节数组
     */
    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.trim().isEmpty()) {
            return null;
        }
        
        try {
            String cleanHex = hexString.replaceAll("\\s+", "").replaceAll("[^0-9A-Fa-f]", "");
            
            if (cleanHex.length() % 2 != 0) {
                System.err.println("十六进制字符串长度不是偶数: " + hexString);
                return null;
            }
            
            byte[] bytes = new byte[cleanHex.length() / 2];
            for (int i = 0; i < bytes.length; i++) {
                int index = i * 2;
                bytes[i] = (byte) Integer.parseInt(cleanHex.substring(index, index + 2), 16);
            }
            
            return bytes;
        } catch (NumberFormatException e) {
            System.err.println("十六进制字符串格式错误: " + hexString);
            return null;
        }
    }
    
    /**
     * 将字节数组转换为十六进制字符串
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}