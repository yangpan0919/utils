package com.example.serialproxy.controller;

import com.example.serialproxy.config.ProxyConfig;
import com.example.serialproxy.util.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api/serial")
public class SerialPortController {

    @Autowired
    private ProxyConfig proxyConfig;

    /**
     * 获取系统中所有可用的串口
     */
    @GetMapping("/ports")
    public ResponseEntity<Map<String, Object>> getAvailablePorts() {
        try {
            List<Map<String, Object>> portList = new ArrayList<>();
            List<SerialPort> ports = SerialPortUtil.getAvailablePorts();
            
            for (SerialPort port : ports) {
                Map<String, Object> portInfo = new HashMap<>();
                portInfo.put("systemPortName", port.getSystemPortName());
                portInfo.put("descriptivePortName", port.getDescriptivePortName());
                portInfo.put("portLocation", port.getPortLocation());
                portInfo.put("isOpen", port.isOpen());
                portInfo.put("baudRate", port.getBaudRate());
                portInfo.put("numDataBits", port.getNumDataBits());
                portInfo.put("numStopBits", port.getNumStopBits());
                portInfo.put("parity", port.getParity());
                portInfo.put("flowControl", port.getFlowControlSettings());
                portList.add(portInfo);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取可用串口成功");
            response.put("data", portList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取可用串口失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取可用串口失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 检查指定串口是否可用
     */
    @GetMapping("/ports/{portName}/check")
    public ResponseEntity<Map<String, Object>> checkPortAvailability(@PathVariable String portName) {
        try {
            boolean available = SerialPortUtil.isPortAvailable(portName);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "检查串口可用性成功");
            Map<String, Object> data = new HashMap<>();
            data.put("portName", portName);
            data.put("available", available);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("检查串口可用性失败: {}", portName, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "检查串口可用性失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 获取当前代理配置
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getProxyConfig() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取代理配置成功");
            response.put("data", proxyConfig);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取代理配置失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取代理配置失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/system/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        try {
            Map<String, Object> systemInfo = new HashMap<>();
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            systemInfo.put("osName", System.getProperty("os.name"));
            systemInfo.put("osVersion", System.getProperty("os.version"));
            systemInfo.put("osArch", System.getProperty("os.arch"));
            systemInfo.put("userHome", System.getProperty("user.home"));
            systemInfo.put("userDir", System.getProperty("user.dir"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取系统信息成功");
            response.put("data", systemInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取系统信息失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取系统信息失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
