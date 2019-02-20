package com.ximuyi.demo.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        /**
         * If a  TextWebSocketFrame is received, call  retain() on it and write and flush it to
         * the  ChannelGroup so every connected WebSockets Channel gets it. Calling
         * retain() is needed because other wise the  TextWebSocketFrame would be
         * released once the  channelRead0(…) method returned. This is a problem as
         * writeAndFlush(…) may complete later.
         */
        group.writeAndFlush(msg.retain());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            ctx.pipeline().remove(HttpRequestHandler.class);
            /**
             * Write a message to all connected WebSocket clients about a new Channel that is now also
             * connected
             */
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
