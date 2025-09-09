//package com.example.tcpproxy;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.io.*;
//import java.net.Socket;
//import java.nio.charset.StandardCharsets;
//
///**
// * TCP客户端测试类
// * 用于测试TCP服务端的功能
// */
//@SpringBootTest
//public class TcpClientTest {
//
//    @Test
//    public void testTcpServer() throws IOException, InterruptedException {
//        // 等待服务端启动
//        Thread.sleep(2000);
//
//        String serverHost = "localhost";
//        int serverPort = 8080;
//
//        try (Socket socket = new Socket(serverHost, serverPort);
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//
//            System.out.println("连接到TCP服务端: " + serverHost + ":" + serverPort);
//
//            // 测试心跳包
//            System.out.println("发送心跳包: 01 02 03 04");
//            sendHexData(socket, "01 02 03 04");
//            Thread.sleep(1000);
//
//            // 测试数据请求
//            System.out.println("发送数据请求: AA BB CC DD");
//            sendHexData(socket, "AA BB CC DD");
//            Thread.sleep(1000);
//
//            // 测试未知数据
//            System.out.println("发送未知数据: FF EE DD CC");
//            sendHexData(socket, "FF EE DD CC");
//            Thread.sleep(1000);
//
//        } catch (Exception e) {
//            System.err.println("测试失败: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 发送十六进制数据
//     */
//    private void sendHexData(Socket socket, String hexString) throws IOException {
//        byte[] data = hexStringToBytes(hexString);
//        if (data != null) {
//            OutputStream out = socket.getOutputStream();
//            out.write(data);
//            out.flush();
//            System.out.println("已发送: " + bytesToHexString(data));
//        }
//    }
//
//    /**
//     * 将十六进制字符串转换为字节数组
//     */
//    private byte[] hexStringToBytes(String hexString) {
//        if (hexString == null || hexString.trim().isEmpty()) {
//            return null;
//        }
//
//        try {
//            String cleanHex = hexString.replaceAll("\\s+", "").replaceAll("[^0-9A-Fa-f]", "");
//
//            if (cleanHex.length() % 2 != 0) {
//                System.err.println("十六进制字符串长度不是偶数: " + hexString);
//                return null;
//            }
//
//            byte[] bytes = new byte[cleanHex.length() / 2];
//            for (int i = 0; i < bytes.length; i++) {
//                int index = i * 2;
//                bytes[i] = (byte) Integer.parseInt(cleanHex.substring(index, index + 2), 16);
//            }
//
//            return bytes;
//        } catch (NumberFormatException e) {
//            System.err.println("十六进制字符串格式错误: " + hexString);
//            return null;
//        }
//    }
//
//    /**
//     * 将字节数组转换为十六进制字符串
//     */
//    private String bytesToHexString(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) {
//            sb.append(String.format("%02X ", b));
//        }
//        return sb.toString().trim();
//    }
//}
