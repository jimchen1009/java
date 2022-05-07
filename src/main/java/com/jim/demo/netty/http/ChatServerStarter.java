package com.jim.demo.netty.http;

import io.netty.channel.ChannelFuture;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;

public class ChatServerStarter {

    public static void main(String[] args) throws GeneralSecurityException {
	    //不使用这个包的内容,引用的jar太多
	    // compile group: 'org.apache.mina', name: 'mina-example', version:'2.0.19'
        //SSLContext context = BogusSslContextFactory.getInstance(true);

	    X509TrustManager x509m = new X509TrustManager() {


		    @Override
		    public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {

		    }

		    @Override
		    public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s){

		    }

		    @Override
		    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			    return new java.security.cert.X509Certificate[0];
		    }
	    };
	    // 获取一个SSLContext实例
	    SSLContext context = SSLContext.getInstance("SSL");
	    // 初始化SSLContext实例
	    context.init(null, new TrustManager[] { x509m }, new java.security.SecureRandom());
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
