package com.jim.demo.netty.spdy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;


public class SpdyChannelInitializer extends ChannelInitializer<Channel> {

    private final SslContext sslCtx;

    public SpdyChannelInitializer(SslContext context) {
        this.sslCtx = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        // Negotiates with the browser if SPDY or HTTP is going to be used
        pipeline.addLast(new SpdyOrHttpHandler());
    }
}
