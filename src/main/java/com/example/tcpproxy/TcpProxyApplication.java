package com.example.tcpproxy;

import com.example.tcpproxy.config.ProxyConfig;
import com.example.tcpproxy.handler.MessageEncoder;
import com.example.tcpproxy.handler.ProxyBackendHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Slf4j
@SpringBootApplication
@Configuration
public class TcpProxyApplication {


    private static Bootstrap bootstrap;
    static EventLoopGroup group;
    private static ProxyConfig proxyConfig;
    public static Channel outboundChannel;
    public static ProxyBackendHandler proxyBackendHandler;


    public static void main(String[] args) {
        //todo 连接plc

        proxyConfig = new ProxyConfig();
        proxyConfig.setRemoteHost("192.168.89.1");
        proxyConfig.setRemotePort(12289);
        proxyBackendHandler = new ProxyBackendHandler(null, 1);


        group = new NioEventLoopGroup();
        //Pipeline deal logic
        bootstrap = new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {

//                ChannelPipeline pipeline = ch.pipeline();
                ch.pipeline().addLast(proxyBackendHandler);
                ch.pipeline().addLast(new MessageEncoder());
            }
        });


        ChannelFuture f = bootstrap.connect(proxyConfig.getRemoteHost(), proxyConfig.getRemotePort());
        outboundChannel = f.channel();

        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // 连接成功，开始读取数据
                log.info("客户端({})已连接到目标服务器: {}:{}", 1, proxyConfig.getRemoteHost(), proxyConfig.getRemotePort());
//                inboundChannel.read();
            } else {
                // 连接失败，关闭入站连接
                log.error("客户端({})连接目标服务器失败: {}:{}", 1, proxyConfig.getRemoteHost(), proxyConfig.getRemotePort(), future.cause());
//                inboundChannel.close();
            }
        });
        System.out.println("++++");

        SpringApplication.run(TcpProxyApplication.class, args);
    }
} 