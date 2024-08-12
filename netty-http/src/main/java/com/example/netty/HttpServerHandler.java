package com.example.netty;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.netty.pojo.User;
import com.example.netty.serialize.impl.JSONSerializer;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;

/**
 * @author pengtao
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);
    private static final String FAVICON_ICO = "/favicon.ico";
    private static final AsciiString CONTENT_TYPE_HDR = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH_HDR = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION_HDR = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE_VAL = AsciiString.cached("keep-alive");

    private HttpRequest request;
    private HttpHeaders headers;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 检查是否为HttpRequest对象
        if (!(msg instanceof HttpRequest)) {
            return; // 如果不是HttpRequest，不进行处理
        }
        // 将HttpObject转换为HttpRequest对象
        request = (HttpRequest) msg;
        // 从HttpRequest对象中获取HttpHeaders
        headers = request.headers();
        // 调用自定义方法来进一步处理请求
        handleRequest(ctx); // ctx是ChannelHandlerContext对象，提供了对Channel的引用
    }

    private void handleRequest(ChannelHandlerContext ctx) throws Exception {
        String uri = request.uri();
        if (uri.equals(FAVICON_ICO)) {
            return;
        }
        HttpMethod method = request.method();
        User user = new User();
        user.setUserName("pengtao");
        user.setDate(new Date());
        user.setMethod(method.name());
        if (method.equals(GET)) {
            handleGetRequest();
        } else if (method.equals(POST)) {
            handlePostRequest(ctx);
        } else {
            throw new UnsupportedOperationException("HTTP method " + method + " is not supported");
        }
        writeResponse(ctx, serializeUser(user));
    }

    private void handleGetRequest() {
        logger.info("get请求....");
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri(), Charsets.UTF_8);
        for (Map.Entry<String, List<String>> param : queryDecoder.parameters().entrySet()) {
            for (String value : param.getValue()) {
                logger.info("{}={}", param.getKey(), value);
            }
        }
    }

    private void handlePostRequest(ChannelHandlerContext ctx) throws Exception {
        logger.info("post请求....");
        dealWithContentType(ctx);
    }

    private void writeResponse(ChannelHandlerContext ctx, byte[] content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.content().writeBytes(content);
        response.headers().set(CONTENT_TYPE_HDR, "application/json; charset=UTF-8");
        response.headers().set(CONTENT_LENGTH_HDR, response.content().readableBytes());
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        response.headers().set(CONNECTION_HDR, keepAlive ? KEEP_ALIVE_VAL : null);
        ctx.writeAndFlush(response)
                .addListener(keepAlive ? ChannelFutureListener.CLOSE : ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    private byte[] serializeUser(User user) {
        try {
            return new JSONSerializer().serialize(user);
        } catch (Exception e) {
            logger.error("Error serializing user", e);
            throw e;
        }
    }

    private void dealWithContentType(ChannelHandlerContext ctx) throws Exception {
        String contentType = getContentType();
        if (Objects.isNull(contentType)) return;
        switch (Objects.requireNonNull(contentType)) {
            case "application/json":
                parseJsonRequest(ctx);
                break;
            case "application/x-www-form-urlencoded":
                parseFormRequest(ctx);
                break;
            case "multipart/form-data":
                parseMultipartRequest(ctx);
                break;
            default:
                logger.warn("Unsupported content type: {}", contentType);
                sendError(ctx);
                break;
        }
    }

    private String getContentType() {
        return headers.get(CONTENT_TYPE_HDR) == null ? null : headers.get(CONTENT_TYPE_HDR).toString().split(";")[0].trim();
    }

    private void parseJsonRequest(ChannelHandlerContext ctx) throws Exception {
    }

    private void parseFormRequest(ChannelHandlerContext ctx) throws Exception {

    }

    private void parseMultipartRequest(ChannelHandlerContext ctx) throws Exception {

    }

    private void sendError(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Exception caught", cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}