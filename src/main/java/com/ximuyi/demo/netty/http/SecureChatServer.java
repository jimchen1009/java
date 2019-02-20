package com.ximuyi.demo.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;

import javax.net.ssl.SSLContext;

public class SecureChatServer extends ChatServer {


    private final SSLContext context;
    public SecureChatServer(SSLContext context) {
        this.context = context;
    }

    @Override
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group) {
        return new SecureChatServerIntializer(group, context);
    }
}
