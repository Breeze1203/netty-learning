package com.example.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/*
定义一个继承自ChannelInitializer<SocketChannel>的ClientInitializer类，
用于初始化SocketChannel的ChannelPipeline
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    // 定义一个静态的StringDecoder实例，用于将字节数据解码为字符串
    private static final StringDecoder DECODER = new StringDecoder();
    // 定义一个静态的StringEncoder实例，用于将字符串编码为字节数据
    private static final StringEncoder ENCODER = new StringEncoder();
    // 定义一个静态的ClientHandler实例，这是自定义的处理器，用于处理具体的业务逻辑
    private static final ClientHandler CLIENT_HANDLER = new ClientHandler();
    // 重写ChannelInitializer的initChannel方法，用于在通道创建时初始化ChannelPipeline
    @Override
    public void initChannel(SocketChannel ch) {
        // 获取SocketChannel的ChannelPipeline
        ChannelPipeline pipeline = ch.pipeline();
        // 向pipeline添加DelimiterBasedFrameDecoder，用于将接收到的字节数据按照行分隔符进行解码
        // 8192是缓冲区大小，Delimiters.lineDelimiter()是行分隔符
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // 向pipeline添加DECODER，用于将解码后的字节数据转换为字符串
        pipeline.addLast(DECODER);
        // 向pipeline添加ENCODER，用于将字符串转换为字节数据，以便发送
        pipeline.addLast(ENCODER);
        // 向pipeline添加CLIENT_HANDLER，这是自定义的处理器，用于处理具体的业务逻辑
        pipeline.addLast(CLIENT_HANDLER);
    }
}
