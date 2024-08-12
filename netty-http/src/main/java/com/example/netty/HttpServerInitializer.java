package com.example.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;


public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        /**
         * HttpServerCodec是Netty提供的HTTP请求和响应编解码器，
         * 它将处理HTTP请求和响应的编码和解码。
         */
        p.addLast(new HttpServerCodec());

        /**
         * HttpObjectAggregator是Netty提供的处理器，用于聚合HTTP片段，
         * 例如将HttpRequest和随后的HttpContent聚合成一个FullHttpRequest，
         * 便于后续处理。这里设置的聚合字节大小为1MB。
         */
        p.addLast(new HttpObjectAggregator(1024 * 1024));

        /**
         * HttpServerExpectContinueHandler用于处理HTTP 'Expect: 100-continue' 头部，
         * 当客户端发送的请求包含这个头部时，服务器将先发送100 Continue响应，
         * 然后客户端再发送请求体。这可以用于支持需要分块传输的大型请求体。
         */
        p.addLast(new HttpServerExpectContinueHandler());

        /**
         * HttpHelloWorldServerHandler是我们自定义的HTTP请求处理器，
         * 它将根据接收到的HTTP请求生成响应。
         */
        p.addLast(new HttpServerHandler());
    }
}