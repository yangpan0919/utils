package com.example.tcpproxy.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import com.example.tcpproxy.config.ProxyConfig;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    private final Bootstrap bootstrap;
    private final ProxyConfig proxyConfig;
    private Channel outboundChannel;
    private static final AtomicInteger clientCounter = new AtomicInteger(0);
    private final int clientId;

    public ProxyFrontendHandler(Bootstrap bootstrap, ProxyConfig proxyConfig) {
        this.bootstrap = bootstrap;
        this.proxyConfig = proxyConfig;
        this.clientId = clientCounter.incrementAndGet();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel inboundChannel = ctx.channel();
        
        log.info("客户端({})已连接: {}", clientId, inboundChannel.remoteAddress());

        // 连接到目标服务器
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast(new ProxyBackendHandler(inboundChannel, clientId));
            }
        });

        ChannelFuture f = bootstrap.connect(proxyConfig.getRemoteHost(), proxyConfig.getRemotePort());
        outboundChannel = f.channel();
        
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // 连接成功，开始读取数据
                log.info("客户端({})已连接到目标服务器: {}:{}", clientId, proxyConfig.getRemoteHost(), proxyConfig.getRemotePort());
                inboundChannel.read();
            } else {
                // 连接失败，关闭入站连接
                log.error("客户端({})连接目标服务器失败: {}:{}", clientId, proxyConfig.getRemoteHost(), proxyConfig.getRemotePort(), future.cause());
                inboundChannel.close();
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel.isActive()) {
            if (msg instanceof ByteBuf) {
                ByteBuf buf = (ByteBuf) msg;
                
                // 打印字节内容 - 十六进制格式
                int readableBytes = buf.readableBytes();
                byte[] bytes = new byte[readableBytes];
                int readerIndex = buf.readerIndex();
                buf.getBytes(readerIndex, bytes);
                
                StringBuilder hexDump = new StringBuilder();
                for (byte b : bytes) {
                    hexDump.append(String.format("%02X ", b));
                }
                
                // 尝试转换为字符串
                String strContent;
                try {
                    strContent = buf.toString(readerIndex, readableBytes, CharsetUtil.UTF_8);
                } catch (Exception e) {
                    strContent = "[无法转换为UTF-8字符串]";
                }
                
                log.info("客户端({}) -> 服务端: 长度={}, 十六进制={}, 字符串={}", 
                    clientId, readableBytes, hexDump.toString(), strContent);
            } else {
                log.info("客户端({}) -> 服务端: {} (非ByteBuf类型)", clientId, msg);
            }
            
            outboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("客户端({})断开连接: {}", clientId, ctx.channel().remoteAddress());
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("前端处理器({})异常", clientId, cause);
        closeOnFlush(ctx.channel());
    }

    /**
     * 刷新所有挂起的消息并关闭通道
     */
    private static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
} 