package com.example.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author 彭涛
 * @date 2024年8月10号
 * @description 服务端启动程序
 */
public final class Server {
    // 程序的主入口点
    public static void main(String[] args) throws Exception {
        // 创建一个NioEventLoopGroup实例，用于处理接受客户端连接的事件，这里只创建一个线程
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 创建另一个NioEventLoopGroup实例，用于处理已接受连接的事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建一个ServerBootstrap实例，用于启动服务器
            ServerBootstrap b = new ServerBootstrap();
            // 配置ServerBootstrap，使用bossGroup来接受客户端连接，使用workerGroup来处理这些连接
            b.group(bossGroup, workerGroup)
                    // 设置服务器端使用的通道类型为NioServerSocketChannel，这是基于NIO的服务器Socket通道
                    .channel(NioServerSocketChannel.class)
                    // 添加一个LoggingHandler处理器，用于记录日志，这里设置日志级别为INFO
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 设置子通道的初始化器，用于初始化每个连接的ChannelPipeline
                    .childHandler(new ServerInitializer());
            // 绑定服务器到8888端口，并返回ChannelFuture对象
            ChannelFuture f = b.bind(8888);
            // 等待服务器关闭的Future完成，这会导致阻塞直到服务器关闭
            f.channel().closeFuture().sync();
        } finally {
            // 无论发生什么，都确保优雅地关闭bossGroup和workerGroup，释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
