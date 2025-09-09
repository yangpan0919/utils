package com.example.tcpproxy.server;

import com.example.tcpproxy.config.TcpServerConfig;
import com.example.tcpproxy.handler.TcpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Netty TCP服务端
 * 用于模拟TCP通讯的服务端，支持可配置的固定字节数据收发
 */
@Slf4j
@Component
public class TcpServer {
    
    @Autowired
    private TcpServerConfig tcpServerConfig;
    
    @Autowired
    private TcpServerHandler tcpServerHandler;
    
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    
    @PostConstruct
    public void start() {
        if (!tcpServerConfig.isEnabled()) {
            log.info("TCP服务端未启用");
            return;
        }
        
        new Thread(() -> {
            try {
                startServer();
            } catch (Exception e) {
                log.error("TCP服务端启动失败", e);
            }
        }, "TcpServer-Thread").start();
    }
    
    private void startServer() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            
                            // 添加日志处理器
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            
                            // 添加长度字段解码器（可选，根据实际协议需求）
                            // pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                            // pipeline.addLast(new LengthFieldPrepender(4));
                            
                            // 添加自定义处理器
                            pipeline.addLast(tcpServerHandler);
                        }
                    });
            
            // 绑定端口并启动服务
            ChannelFuture future = bootstrap.bind(tcpServerConfig.getPort()).sync();
            serverChannel = future.channel();
            
            log.info("TCP服务端启动成功，监听端口: {}", tcpServerConfig.getPort());
            
            // 等待服务器关闭
            serverChannel.closeFuture().sync();
            
        } finally {
            shutdown();
        }
    }
    
    @PreDestroy
    public void shutdown() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        
        log.info("TCP服务端已关闭");
    }
    
    /**
     * 检查服务端是否运行中
     */
    public boolean isRunning() {
        return serverChannel != null && serverChannel.isActive();
    }
    
    /**
     * 获取服务端端口
     */
    public int getPort() {
        return tcpServerConfig.getPort();
    }
}
