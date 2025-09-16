# 串口模拟服务 (Serial Port Simulator)

一个基于Spring Boot的串口模拟服务，用于模拟串口设备的行为。当接收到预定义的固定消息时，会自动回复对应的固定响应消息。

## 功能特性

- 支持串口参数配置（波特率、数据位、停止位、校验位等）
- 支持预定义的消息匹配和自动回复
- 支持十六进制格式的消息配置
- 完整的日志记录功能
- 基于jSerialComm库实现跨平台串口通信

## 技术栈

- Java 8
- Spring Boot 2.7.5
- jSerialComm 2.9.3
- Lombok
- Logback

## 快速开始

### 1. 环境要求
- Java 8+
- Maven 3.6+
- 可用的串口设备

### 2. 编译运行
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

### 3. 配置说明

在 `src/main/resources/application.yml` 中配置串口参数：

```yaml
serial:
  server:
    enabled: true
    portName: "COM1"        # 串口名称
    baudRate: 9600          # 波特率
    dataBits: 8             # 数据位
    stopBits: 1             # 停止位
    parity: 0               # 校验位 (0=无校验, 1=奇校验, 2=偶校验)
    flowControl: 0          # 流控制 (0=无流控制, 1=RTS/CTS, 2=XON/XOFF)
    readTimeout: 1000       # 读取超时时间（毫秒）
    # 接收的固定字节数据配置
    receiveData:
      - name: "数据包1"
        hexData: "30 31 57 52 44 44 30 31 31 31 31 2C 31 0D 0A"
        enabled: true
    # 发送的固定字节数据配置
    sendData:
      - name: "数据包1"
        hexData: "31 31 4F 4B 30 30 37 30 0D 0A"
        enabled: true
```

## 项目结构

```
src/main/java/com/example/tcpproxy/
├── TcpProxyApplication.java          # 主启动类
├── config/
│   └── SerialServerConfig.java       # 串口服务配置类
├── handler/
│   └── SerialMessageHandler.java     # 串口消息处理器
└── server/
    └── SerialServer.java             # 串口服务实现
```

## 配置参数详解

### SerialServerConfig 配置项

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| enabled | boolean | true | 是否启用串口服务 |
| portName | String | "COM1" | 串口名称 |
| baudRate | int | 9600 | 波特率 |
| dataBits | int | 8 | 数据位 |
| stopBits | int | 1 | 停止位 |
| parity | int | 0 | 校验位 |
| flowControl | int | 0 | 流控制 |
| readTimeout | int | 1000 | 读取超时时间（毫秒） |
| receiveData | List<ByteDataConfig> | - | 接收数据配置列表 |
| sendData | List<ByteDataConfig> | - | 发送数据配置列表 |

### ByteDataConfig 配置项

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| name | String | - | 数据名称/描述 |
| hexData | String | - | 十六进制字符串，如 "01 02 03 04" |
| enabled | boolean | true | 是否启用此配置 |

## 工作原理

1. **服务启动**: 根据配置打开指定的串口
2. **数据监听**: 持续监听串口接收到的数据
3. **数据匹配**: 根据配置的receiveData列表匹配接收到的数据
4. **数据回复**: 找到匹配的配置后，根据name查找对应的sendData配置
5. **数据发送**: 将配置的回复数据发送到串口
6. **日志记录**: 记录所有收发数据的详细信息

## 使用方法

1. 确保系统已安装Java 8或更高版本
2. 配置 `application.yml` 中的串口参数和消息配置
3. 运行应用：
   ```bash
   mvn spring-boot:run
   ```
4. 应用将自动打开指定的串口并开始监听消息
5. 当接收到匹配的消息时，会自动发送对应的回复消息

## 日志说明

项目使用Logback进行日志管理，日志文件保存在 `logs/tcp-proxy.log`：

- **INFO级别**: 服务启动、串口连接等关键信息
- **DEBUG级别**: 详细的数据收发记录
- **WARN级别**: 配置错误、数据格式错误等警告
- **ERROR级别**: 串口异常、处理错误等错误信息

## 扩展功能

### 1. 添加新的数据配置

在 `application.yml` 中添加新的收发数据配置：

```yaml
serial:
  server:
    receiveData:
      - name: "新命令"
        hexData: "FF EE DD CC"
        enabled: true
    sendData:
      - name: "新命令"
        hexData: "11 22 33 44"
        enabled: true
```

### 2. 自定义处理器

可以继承 `SerialMessageHandler` 类，重写相关方法实现自定义逻辑：

```java
@Component
public class CustomSerialMessageHandler extends SerialMessageHandler {
    
    @Override
    public byte[] handleMessage(byte[] receivedData) {
        // 自定义处理逻辑
        return super.handleMessage(receivedData);
    }
}
```

## 注意事项

1. **串口占用**: 确保指定的串口未被其他程序占用
2. **数据格式**: 十六进制消息格式要正确，支持空格分隔
3. **配置匹配**: receiveData和sendData的name要一一对应
4. **串口权限**: 确保程序有访问串口的权限
5. **日志监控**: 定期检查日志文件，监控服务运行状态

## 故障排查

### 常见问题

1. **串口服务启动失败**
   - 检查串口名称是否正确
   - 检查串口是否被其他程序占用
   - 检查配置文件格式是否正确

2. **串口连接失败**
   - 检查串口设备是否正常
   - 检查串口参数配置是否正确
   - 确认串口驱动已正确安装

3. **数据收发异常**
   - 检查十六进制数据格式
   - 查看日志中的错误信息
   - 确认配置的name匹配关系

## 技术实现

项目使用Java开发，实现了串口模拟服务器，支持：
- 跨平台串口通信（基于jSerialComm）
- 消息解析和自动回复
- 日志记录和监控
- 错误处理和重连机制

## 历史版本

### v2.0.0 (当前版本)
- 基于jSerialComm的串口服务实现
- 支持可配置的固定字节数据收发
- 完整的日志记录和监控
- Spring Boot集成

### v1.0.0 (历史版本)
- 基于Netty的TCP服务端实现
- 支持可配置的固定字节数据收发
- 完整的日志记录和监控
- Spring Boot集成