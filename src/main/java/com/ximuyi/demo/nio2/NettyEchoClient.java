package com.ximuyi.demo.nio2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class NettyEchoClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyEchoClient.class);

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>(){
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LineBasedFrameDecoder(20 * 1024));
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                byte[] bytes = ByteBufUtil.getBytes(msg);
                                logger.debug("{}", new String(bytes, Charset.forName("UTF-8")));
                                ByteBuf byteBuf = Unpooled.copiedBuffer("Hi, I'am Jim!\r\n", Charset.forName("UTF-8"));
                                ctx.channel().writeAndFlush(byteBuf);
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                            }
                        });
                    }
                });
        ;
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8001));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    logger.debug("channel={} established", future.channel());
                }
                else {
                    logger.error("channel={} connection attempt failed", future.channel(), future.cause());
                }
            }
        });
        future.channel().closeFuture().sync();
        logger.debug("client is shutdowned~");
    }
}
