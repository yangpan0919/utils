package com.example.tcpproxy.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.tcpproxy.config.ProxyConfig;
import com.example.tcpproxy.handler.ProxyFrontendHandler;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class TcpProxyServer {

    @Autowired
    private ProxyConfig proxyConfig;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    @PostConstruct
    public void start() throws Exception {
        log.info("启动TCP代理服务器，监听端口: {}", proxyConfig.getLocalPort());
        
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // 创建与目标服务器的引导程序
                        Bootstrap bootstrap = new Bootstrap();
                        bootstrap.group(workerGroup)
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.TCP_NODELAY, true)
                            .option(ChannelOption.SO_KEEPALIVE, true);
                        
                        ch.pipeline().addLast(new ProxyFrontendHandler(bootstrap, proxyConfig));
                    }
                })
                .childOption(ChannelOption.AUTO_READ, false);
            
            // 绑定端口，开始接收连接
            channelFuture = b.bind(proxyConfig.getLocalPort()).sync();
            log.info("TCP代理服务器已启动并监听在 {}", proxyConfig.getLocalPort());
            
        } catch (Exception e) {
            log.error("TCP代理服务器启动失败", e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw e;
        }
    }

    @PreDestroy
    public void stop() {
        log.info("关闭TCP代理服务器...");
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        
        if (bossGroup != null && workerGroup != null) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        log.info("TCP代理服务器已关闭");
    }
} 