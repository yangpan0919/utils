package com.example.tcpproxy;

import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * 串口客户端测试类
 * 用于测试串口模拟服务的功能
 */
@Slf4j
public class SerialClientTest {
    
    public static void main(String[] args) {
        // 配置串口参数
        String portName = "COM1";
        int baudRate = 9600;
        
        SerialPort serialPort = SerialPort.getCommPort(portName);
        
        if (serialPort == null) {
            log.error("无法找到串口: {}", portName);
            return;
        }
        
        // 配置串口参数
        serialPort.setBaudRate(baudRate);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(1);
        serialPort.setParity(0);
        serialPort.setFlowControl(0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        
        // 打开串口
        if (!serialPort.openPort()) {
            log.error("无法打开串口: {}", portName);
            return;
        }
        
        log.info("串口客户端启动成功，串口: {} 波特率: {}", portName, baudRate);
        
        Scanner scanner = new Scanner(System.in);
        
        try {
            while (true) {
                System.out.println("\n请选择操作:");
                System.out.println("1. 发送数据包1");
                System.out.println("2. 发送数据包2");
                System.out.println("3. 发送数据包3");
                System.out.println("4. 发送数据包4");
                System.out.println("5. 发送数据包5");
                System.out.println("0. 退出");
                System.out.print("请输入选择: ");
                
                String choice = scanner.nextLine();
                
                byte[] sendData = null;
                
                switch (choice) {
                    case "1":
                        sendData = hexStringToBytes("30 31 57 52 44 44 30 31 31 31 31 2C 31 0D 0A");
                        break;
                    case "2":
                        sendData = hexStringToBytes("30 31 57 52 44 44 30 31 31 31 32 2C 31 30 0D 0A");
                        break;
                    case "3":
                        sendData = hexStringToBytes("30 31 57 52 44 44 30 31 32 31 33 2C 31 0D 0A");
                        break;
                    case "4":
                        sendData = hexStringToBytes("30 31 57 52 44 44 30 31 32 31 34 2C 31 0D 0A");
                        break;
                    case "5":
                        sendData = hexStringToBytes("30 31 57 52 57 30 31 44 30 30 30 30 33 2C 30 30 31 45 0D 0A");
                        break;
                    case "0":
                        System.out.println("退出程序");
                        return;
                    default:
                        System.out.println("无效选择，请重新输入");
                        continue;
                }
                
                if (sendData != null) {
                    // 发送数据
                    int bytesWritten = serialPort.writeBytes(sendData, sendData.length);
                    if (bytesWritten > 0) {
                        log.info("发送数据: {}", bytesToHexString(sendData));
                        
                        // 等待回复
                        byte[] buffer = new byte[1024];
                        int bytesRead = serialPort.readBytes(buffer, buffer.length);
                        
                        if (bytesRead > 0) {
                            byte[] receivedData = new byte[bytesRead];
                            System.arraycopy(buffer, 0, receivedData, 0, bytesRead);
                            log.info("接收回复: {}", bytesToHexString(receivedData));
                        } else {
                            log.warn("未收到回复数据");
                        }
                    } else {
                        log.error("数据发送失败");
                    }
                }
            }
        } catch (Exception e) {
            log.error("测试过程中发生异常", e);
        } finally {
            if (serialPort.isOpen()) {
                serialPort.closePort();
                log.info("串口已关闭");
            }
            scanner.close();
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
    
    /**
     * 将十六进制字符串转换为字节数组
     */
    private static byte[] hexStringToBytes(String hexString) {
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
