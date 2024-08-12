package com.example.netty;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
// 创建一个日志记录器实例，用于记录日志信息。
    static final int PORT = 8888;
// 定义服务器监听的端口号为8888。
    public static void main(String[] args) throws Exception {
        /*
        bossGroup：这个事件循环组专门用于接受客户端的连接。在你的代码中，bossGroup 是通过 new NioEventLoopGroup(1) 创建的，
        这意味着它只有一个线程。这个单线程（或单个EventLoop）将负责监听服务器端口，并接受所有进入的连接。
        由于它只处理连接的接受，而不处理其他I/O操作，通常一个线程就足够了，这也是为什么你看到创建时传递了 1 作为参数。
        workerGroup：这个事件循环组用于处理已被 bossGroup 接受的连接之后的所有I/O操作。
        例如，它将负责读取请求数据、处理请求和发送响应。在你的代码中，workerGroup 是通过 new NioEventLoopGroup() 创建的，
        没有指定线程数量，这意味着Netty将根据默认的I/O工作器数量来创建EventLoops。这个组可以根据你的服务器硬件和负载情况配置多个线程，
        以提高并发处理能力。
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 监听端口
        EventLoopGroup workerGroup = new NioEventLoopGroup(5); // 处理连接
        try {
            ServerBootstrap b = new ServerBootstrap();
            // 使用ServerBootstrap辅助启动类来初始化服务器。
            b.option(ChannelOption.SO_BACKLOG, 1024);
            // 设置服务器可接受的连接队列大小为1024。
            b.childOption(ChannelOption.TCP_NODELAY, true);
            // 设置TCP_NODELAY选项为true，禁用Nagle算法，确保数据立即发送。
            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 设置SO_KEEPALIVE选项为true，启用TCP保活机制。
            b.group(bossGroup, workerGroup)
                    // 设置接受连接的事件循环组和处理连接的事件循环组。
                    .channel(NioServerSocketChannel.class)
                    // 设置用于创建服务器通道的类。
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 添加日志处理器，记录服务器的日志信息。
                    .childHandler(new HttpServerInitializer());
            // 设置用于初始化每个新连接的Channel的ChannelInitializer。
            Channel ch = b.bind(PORT).sync().channel();
            // 绑定服务器到指定端口，并同步等待直到绑定成功。
            logger.info("Netty http server listening on port " + PORT);
            // 记录日志信息，告知服务器正在监听指定端口。
            ch.closeFuture().sync();
            // 等待直到Channel关闭。
        } finally {
            bossGroup.shutdownGracefully();
            // 优雅地关闭bossGroup，释放资源。
            workerGroup.shutdownGracefully();
            // 优雅地关闭workerGroup，释放资源。
        }
    }
}
