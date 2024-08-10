package com.example.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    // 定义一个静态的StringDecoder实例，用于将接收到的字节数据解码为字符串
    private static final StringDecoder DECODER = new StringDecoder();
    // 定义一个静态的StringEncoder实例，用于将字符串编码为字节数据，以便发送
    private static final StringEncoder ENCODER = new StringEncoder();
    // 定义一个静态的ServerHandler实例，这是自定义的处理器，用于处理具体的业务逻辑
    private static final ServerHandler SERVER_HANDLER = new ServerHandler();

    // 重写initChannel方法，用于在SocketChannel创建时初始化ChannelPipeline
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // 获取SocketChannel的ChannelPipeline
        ChannelPipeline pipeline = ch.pipeline();
        // 首先添加DelimiterBasedFrameDecoder，用于将接收到的字节数据按照行分隔符进行解码
        // 8192是解码器使用的缓冲区大小，Delimiters.lineDelimiter()定义了行分隔符
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // 然后添加DECODER，将解码后的字节数据转换为字符串
        pipeline.addLast(DECODER);
        // 添加ENCODER，将需要发送的字符串转换为字节数据
        pipeline.addLast(ENCODER);

        // 最后添加SERVER_HANDLER，这是自定义的业务逻辑处理器
        // 由于DECODER和ENCODER是线程安全的，并且可以被多个Channel共享，所以它们被声明为静态的
        pipeline.addLast(SERVER_HANDLER);
    }
}