package com.example.netty.client;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;


// 使用@Sharable注解表明这个handler是可共享的，即多个Channel可以共享同一个handler实例
@Sharable
public class ClientHandler extends SimpleChannelInboundHandler<String> {
    // 重写channelRead0方法，这是SimpleChannelInboundHandler的抽象方法，用于处理接收到的消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 打印接收到的消息到标准错误输出
        System.err.println(msg);
    }

    // 重写exceptionCaught方法，这是ChannelInboundHandler的抽象方法，用于处理通道中的异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 打印异常堆栈到标准错误输出
        cause.printStackTrace();
        // 关闭发生异常的ChannelHandlerContext关联的通道
        ctx.close();
    }
}
/*
`@Sharable`：这个注解表明 `ClientHandler` 类的实例可以在多个通道之间共享，而不需要为每个通道创建一个新的实例。这有助于节省资源。
ClientHandler` 类继承自 `SimpleChannelInboundHandler`，这是一个简化版的处理器，只处理一种类型的消息。在这个例子中，它处理的是 `String` 类型的消息。
`channelRead0` 方法是 `SimpleChannelInboundHandler` 的一个抽象方法，需要被重写。这个方法会在通道读取到数据时被调用。
在这个例子中，它接收到的消息被简单地打印到标准错误输出。
`exceptionCaught` 方法同样是需要被重写的抽象方法，用于处理在通道处理过程中发生的异常。在这个例子中，当捕获到异常时，它会打印异常的堆栈跟踪，并关闭当前的 `ChannelHandlerContext` 关联的通道，以释放资源并防止资源泄露。
总的来说，`ClientHandler` 类通过重写这两个方法，实现了对客户端接收到的消息的简单处理，以及对异常情况的响应。

 */