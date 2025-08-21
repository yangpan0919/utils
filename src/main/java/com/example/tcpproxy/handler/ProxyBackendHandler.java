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


    byte[] buffer1 = new byte[102400];

    int index1 = 0;

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


            System.arraycopy(bytes, 0, buffer1, index1, readableBytes);

            index1 += readableBytes;

            if (index1 > 2) {
                byte b1 = buffer1[index1 - 2];
                byte b2 = buffer1[index1 - 1];
                if (b1 == 0x0D && b2 == 0x0A) {
                    //转发数据
                    // 转发数据到目标串口
                    log.info("开发发送数据");
                    byte[] bytes1 = new byte[index1];

                    System.arraycopy(buffer1, 0, bytes1, 0, index1);

                    inboundChannel.writeAndFlush(bytes1).addListener((ChannelFutureListener) future -> {
//                            if (future.isSuccess()) {
//                                ctx.channel().read();
//                            } else {
//                                future.channel().close();
//                            }
                    });
                    index1 = 0;
                    return;

                }
            }

        } else {
            log.info("服务端 -> 客户端({}): {} (非ByteBuf类型)", clientId, msg);
        }
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