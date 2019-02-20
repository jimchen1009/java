package com.ximuyi.demo.netty.http;

import io.netty.channel.ChannelFuture;
import org.apache.mina.example.echoserver.ssl.BogusSslContextFactory;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;

public class ChatServerStarter {

    public static void main(String[] args) throws GeneralSecurityException {
        SSLContext context = BogusSslContextFactory.getInstance(true);
        final SecureChatServer endpoint = new SecureChatServer(context);
        ChannelFuture future = endpoint.start(new InetSocketAddress(8001));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                endpoint.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }
}
