package com.example.serialproxy.server;

import com.example.serialproxy.config.ProxyConfig;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class SerialProxyServer {

    @Autowired
    private ProxyConfig proxyConfig;

    private SerialPort sourcePort;
    private SerialPort targetPort;
    private ExecutorService executorService;
    private volatile boolean running = false;

    @PostConstruct
    public void start() throws Exception {

//        new Thread(() -> {
//            try {
//                Thread.sleep(3000L);
//            } catch (InterruptedException e) {
//            }
//            OutputStream outputStream = sourcePort.getOutputStream();
//            byte[] buffer = new byte[1024];
//            for (int i = 0; i < 10; i++) {
//                buffer[i] = (byte) i;
//            }
//            try {
//                // 转发数据到目标串口
//                log.info("源串口->目标串口 发送数据：0-9");
//                outputStream.write(buffer, 0, 10);
//                outputStream.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
        log.info("启动串口代理服务器...");
        log.info("源串口: {}", proxyConfig.getSourcePort().getPortName());
        log.info("目标串口: {}", proxyConfig.getTargetPort().getPortName());

        executorService = Executors.newFixedThreadPool(2);

        try {
            // 初始化源串口
            sourcePort = initializeSerialPort(proxyConfig.getSourcePort(), "源串口");
            if (sourcePort == null) {
                log.warn("源串口 {} 不可用，程序将以模拟模式运行", proxyConfig.getSourcePort().getPortName());
                // 不抛出异常，允许程序继续运行
            }

            // 初始化目标串口
            targetPort = initializeSerialPort(proxyConfig.getTargetPort(), "目标串口");
            if (targetPort == null) {
                log.warn("目标串口 {} 不可用，程序将以模拟模式运行", proxyConfig.getTargetPort().getPortName());
                // 不抛出异常，允许程序继续运行
            }

            if (sourcePort != null && targetPort != null) {
                running = true;
                // 启动数据转发任务
                startDataForwarding();
                log.info("串口代理服务器启动成功，开始数据转发");
            } else {
                log.warn("串口代理服务器以模拟模式启动，等待串口可用...");
                // 启动监控线程，等待串口可用
                startPortMonitoring();
            }

        } catch (Exception e) {
            log.error("串口代理服务器启动失败", e);
            cleanup();
            throw e;
        }
    }

    @PreDestroy
    public void stop() {
        log.info("关闭串口代理服务器...");
        running = false;
        cleanup();
        log.info("串口代理服务器已关闭");
    }

    private SerialPort initializeSerialPort(ProxyConfig.SerialPortConfig config, String portType) {
        SerialPort port = SerialPort.getCommPort(config.getPortName());

        if (!port.isOpen()) {
            if (!port.openPort()) {
                log.error("{} 打开失败: {}", portType, config.getPortName());
                return null;
            }
        }

        // 配置串口参数
        port.setBaudRate(config.getBaudRate());
        port.setNumDataBits(config.getDataBits());
        port.setNumStopBits(config.getStopBits());
        port.setParity(config.getParity());
        port.setFlowControl(config.getFlowControl());

        // 设置读取超时
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        log.info("{} 初始化成功: {} (波特率: {}, 数据位: {}, 停止位: {}, 校验位: {})",
                portType, config.getPortName(), config.getBaudRate(), config.getDataBits(),
                config.getStopBits(), config.getParity());
        return port;
    }

    private void startDataForwarding() {
        // 源串口到目标串口的数据转发
        executorService.submit(() -> {
            try {
                log.info("源串口 -> 目标串口 进程开始...");
                forwardData1("源串口 -> 目标串口");
            } catch (Exception e) {
                log.error("源串口到目标串口数据转发异常", e);
            }
        });

        // 目标串口到源串口的数据转发
        executorService.submit(() -> {
            try {
                log.info("目标串口 -> 源串口 进程开始...");
                forwardData2("目标串口 -> 源串口");
            } catch (Exception e) {
                log.error("目标串口到源串口数据转发异常", e);
            }
        });
    }

    private void startPortMonitoring() {
        executorService.submit(() -> {
            while (!running) {
                try {
                    log.info("监控串口状态，等待串口可用...");

                    // 尝试重新初始化源串口
                    if (sourcePort == null) {
                        sourcePort = initializeSerialPort(proxyConfig.getSourcePort(), "源串口");
                        if (sourcePort != null) {
                            log.info("源串口 {} 现在可用了", proxyConfig.getSourcePort().getPortName());
                        }
                    }

                    // 尝试重新初始化目标串口
                    if (targetPort == null) {
                        targetPort = initializeSerialPort(proxyConfig.getTargetPort(), "目标串口");
                        if (targetPort != null) {
                            log.info("目标串口 {} 现在可用了", proxyConfig.getTargetPort().getPortName());
                        }
                    }

                    // 如果两个串口都可用，开始数据转发
                    if (sourcePort != null && targetPort != null) {
                        running = true;
                        startDataForwarding();
                        log.info("串口代理服务器现在开始数据转发");
                        break;
                    }

                    Thread.sleep(5000); // 每5秒检查一次

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("串口监控异常", e);
                    try {
                        Thread.sleep(10000); // 出错后等待10秒再试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });
    }

    private void forwardData1(String direction) {
        byte[] buffer = new byte[1024];
        InputStream inputStream = sourcePort.getInputStream();
        OutputStream outputStream = targetPort.getOutputStream();
        while (running && sourcePort.isOpen() && targetPort.isOpen()) {
            try {

//                while (true) {
//                    int bytesRead = inputStream.available();
//                    if (bytesRead > 0) {
//                        log.info("源串口,有数据长度：" + bytesRead);
//                        break;
//                    }
//                }
                int bytesRead = inputStream.read(buffer);

                // 记录接收到的数据
                logDataReceived(direction, buffer, bytesRead);

                // 转发数据到目标串口
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();

                log.info("{} 转发数据: {} 字节", direction, bytesRead);


                Thread.sleep(10); // 短暂休眠避免CPU占用过高

            } catch (IOException e) {
                if (running) {
                    log.error("{} 数据转发IO异常", direction, e);
                }
                break;
            } catch (Exception e) {
                log.error("{} 数据转发异常", direction, e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void forwardData2(String direction) {
        byte[] buffer = new byte[1024];
        InputStream inputStream = targetPort.getInputStream();
        OutputStream outputStream = sourcePort.getOutputStream();
        while (running && sourcePort.isOpen() && targetPort.isOpen()) {
            try {

//                while (true) {
//                    int bytesRead = inputStream.available();
//                    if (bytesRead > 0) {
//                        log.info("目标串口,有数据长度：" + bytesRead);
//                        break;
//                    }
//                }
                int bytesRead = inputStream.read(buffer);

                // 记录接收到的数据
                logDataReceived(direction, buffer, bytesRead);

                // 转发数据到目标串口
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();

                log.info("{} 转发数据: {} 字节", direction, bytesRead);


                Thread.sleep(10); // 短暂休眠避免CPU占用过高

            } catch (IOException e) {
                if (running) {
                    log.error("{} 数据转发IO异常", direction, e);
                }
                break;
            } catch (Exception e) {
                log.error("{} 数据转发异常", direction, e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void logDataReceived(String direction, byte[] data, int length) {
        StringBuilder hexDump = new StringBuilder();
        StringBuilder asciiDump = new StringBuilder();

        for (int i = 0; i < length && i < data.length; i++) {
            hexDump.append(String.format("%02X ", data[i] & 0xFF));

            // 只显示可打印的ASCII字符
            if (data[i] >= 32 && data[i] <= 126) {
                asciiDump.append((char) data[i]);
            } else {
                asciiDump.append('.');
            }
        }

        log.info("{} 接收数据: 长度={}, 十六进制={}, ASCII={}",
                direction, length, hexDump.toString(), asciiDump.toString());
    }

    private void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
        }

        if (sourcePort != null && sourcePort.isOpen()) {
            sourcePort.closePort();
            log.info("源串口已关闭");
        }

        if (targetPort != null && targetPort.isOpen()) {
            targetPort.closePort();
            log.info("目标串口已关闭");
        }
    }
}
