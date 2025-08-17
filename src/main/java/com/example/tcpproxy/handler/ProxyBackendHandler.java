package com.example.tcpproxy.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyBackendHandler extends ChannelInboundHandlerAdapter {

    private final Channel inboundChannel;
    private final int clientId;

    public ProxyBackendHandler(Channel inboundChannel, int clientId) {
        this.inboundChannel = inboundChannel;
        this.clientId = clientId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
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
            
            log.info("服务端 -> 客户端({}): 长度={}, 十六进制={}, 字符串={}", 
                clientId, readableBytes, hexDump.toString(), strContent);
        } else {
            log.info("服务端 -> 客户端({}): {} (非ByteBuf类型)", clientId, msg);
        }
        
        inboundChannel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                ctx.channel().read();
            } else {
                future.channel().close();
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("目标服务器断开与客户端({})的连接", clientId);
        closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("后端处理器({})异常", clientId, cause);
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