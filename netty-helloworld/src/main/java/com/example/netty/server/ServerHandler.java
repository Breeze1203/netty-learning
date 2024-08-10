package com.example.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.SimpleChannelInboundHandler;
import java.net.InetAddress;
import java.util.Date;


// 使用@Sharable注解表明这个handler是可共享的，即多个Channel可以共享同一个handler实例
@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    // 当Channel处于活跃状态时，即连接建立后，这个方法会被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 获取本地主机的名称
        String hostname = InetAddress.getLocalHost().getHostName();
        // 向客户端发送欢迎消息，包括主机名和当前时间
        ctx.write("Welcome to " + hostname + "!\r\n");
        ctx.write("It is " + new Date() + " now.\r\n");
        // 将数据写入Channel，并刷新发送缓冲区，确保数据被发送出去
        ctx.flush();
    }

    // 当Channel读取到数据时，这个方法会被调用
    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
        // 初始化响应消息和关闭连接的标志
        String response;
        boolean close = false;
        // 根据接收到的请求内容生成响应
        if (request.isEmpty()) {
            response = "Please type something.\r\n"; // 如果请求为空，则提示用户输入内容
        } else if ("bye".equals(request.toLowerCase())) {
            response = "Have a good day!\r\n"; // 如果用户输入"bye"，则生成告别消息
            close = true; // 设置关闭连接的标志
        } else {
            response = "Did you say '" + request + "'?\r\n"; // 否则，生成确认用户输入的消息
        }
        // 将响应消息写入Channel
        ChannelFuture future = ctx.write(response);
        // 如果设置了关闭连接的标志，则添加一个监听器，在消息发送后关闭Channel
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    // 当Channel读取完数据后，这个方法会被调用
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 刷新发送缓冲区，确保数据被发送出去
        ctx.flush();
    }

    // 当Channel中捕获到异常时，这个方法会被调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 打印异常堆栈跟踪
        cause.printStackTrace();
        // 关闭发生异常的ChannelHandlerContext关联的Channel
        ctx.close();
    }
}