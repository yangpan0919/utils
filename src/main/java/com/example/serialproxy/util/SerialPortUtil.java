package com.example.serialproxy.util;

import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SerialPortUtil {

    /**
     * 获取系统中所有可用的串口
     */
    public static List<SerialPort> getAvailablePorts() {
        List<SerialPort> availablePorts = new ArrayList<>();
        SerialPort[] ports = SerialPort.getCommPorts();
        
        for (SerialPort port : ports) {
            availablePorts.add(port);
            log.info("发现串口: {} - {}", port.getSystemPortName(), port.getDescriptivePortName());
        }
        
        return availablePorts;
    }

    /**
     * 根据串口名称获取串口
     */
    public static SerialPort getPortByName(String portName) {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (port.getSystemPortName().equalsIgnoreCase(portName)) {
                return port;
            }
        }
        return null;
    }

    /**
     * 检查串口是否可用
     */
    public static boolean isPortAvailable(String portName) {
        SerialPort port = getPortByName(portName);
        return port != null;
    }

    /**
     * 获取串口状态信息
     */
    public static String getPortStatus(SerialPort port) {
        if (port == null) {
            return "串口为空";
        }
        
        StringBuilder status = new StringBuilder();
        status.append("串口名称: ").append(port.getSystemPortName()).append("\n");
        status.append("描述: ").append(port.getDescriptivePortName()).append("\n");
        status.append("位置: ").append(port.getPortLocation()).append("\n");
        status.append("是否打开: ").append(port.isOpen()).append("\n");
        status.append("波特率: ").append(port.getBaudRate()).append("\n");
        status.append("数据位: ").append(port.getNumDataBits()).append("\n");
        status.append("停止位: ").append(port.getNumStopBits()).append("\n");
        status.append("校验位: ").append(port.getParity()).append("\n");
        status.append("流控制: ").append(port.getFlowControlSettings()).append("\n");
        
        return status.toString();
    }
}
