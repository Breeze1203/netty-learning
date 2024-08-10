package com.example.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class Client {
    public static void main(String[] args) throws Exception {
        // 创建一个NioEventLoopGroup实例，用于处理网络事件的事件循环组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 使用Bootstrap类创建客户端启动引导，用于配置客户端的启动参数
            Bootstrap b = new Bootstrap();
            // 配置Bootstrap的group属性，设置使用的事件循环组
            b.group(group)
                    // 设置客户端使用的通道类型为NioSocketChannel，即基于NIO的Socket通道
                    .channel(NioSocketChannel.class)
                    // 设置客户端初始化器，用于配置客户端通道的pipeline，即处理网络事件的处理器链
                    .handler(new ClientInitializer());
            // 同步连接到指定的服务器地址和端口，并获取连接后的Channel对象
            Channel ch = b.connect("127.0.0.1", 8888).sync().channel();
            // 声明一个ChannelFuture类型的变量，用于存储写操作的Future对象
            ChannelFuture lastWriteFuture = null;
            // 创建BufferedReader对象，用于从标准输入读取数据
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            // 无限循环，从标准输入读取数据，并发送到服务器
            for (;;) {
                // 读取一行文本
                String line = in.readLine();
                // 如果读取到null，说明输入结束，退出循环
                if (line == null) {
                    break;
                }
                // 将读取到的文本发送到服务器，使用writeAndFlush方法，同时将结果存储在lastWriteFuture变量中
                lastWriteFuture = ch.writeAndFlush(line + "\r\n");
                // 如果输入的文本是"bye"，执行以下操作：
                if ("bye".equals(line.toLowerCase())) {
                    // 等待服务器关闭连接
                    ch.closeFuture().sync();
                    // 退出循环
                    break;
                }
            }
            // 如果存在未完成的写操作，等待它们完成
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } finally {
            // 无论发生什么，都确保优雅地关闭事件循环组，释放资源
            group.shutdownGracefully();
        }
    }
}
