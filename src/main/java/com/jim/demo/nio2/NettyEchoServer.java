package com.jim.demo.nio2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NettyEchoServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyEchoServer.class);

    public void server(int port) throws Exception {
        final ByteBuf buf = Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8"));
        EventLoopGroup group = new NioEventLoopGroup();
//        EventLoopGroup group = new OioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group);
            b.channel(NioServerSocketChannel.class);
            b.localAddress(new InetSocketAddress(port));
            b.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                public void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LineBasedFrameDecoder(65 * 1024));

                    ch.pipeline().addLast(new MessageToMessageDecoder<ByteBuf>() {
                        @Override
                        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
                            byte[] bytes = ByteBufUtil.getBytes(msg);
                            out.add(new String(bytes, Charset.forName("UTF-8")));
                        }
                    });

                    ch.pipeline().addLast(new SimpleChannelInboundHandler<String>(){

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            super.channelRead(ctx, msg);
                        }

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                            logger.debug("channelRead0:{}", msg);
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            super.channelActive(ctx);
                            ChannelFuture future = ctx.channel().writeAndFlush(buf.duplicate());
                            future.addListener((ChannelFutureListener) future1 -> System.out.println("writeAndFlush success."));
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            super.exceptionCaught(ctx, cause);
                            logger.error("", cause);
                        }
                    });

                    ch.pipeline().addLast(new ChunkedWriteHandler());
                    ch.pipeline().addLast( new IdleStateHandler(0, 0, 5, TimeUnit.SECONDS));
                    ch.pipeline().addLast( new ChannelInboundHandlerAdapter(){
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            super.userEventTriggered(ctx, evt);
                            if (evt instanceof IdleStateEvent){
                                InputStream stream = Resources.getResourceAsStream("common.txt");
                                ctx.writeAndFlush(new ChunkedStream(stream)).addListener(ChannelFutureListener.CLOSE);
                            }
                            else {
                                super.userEventTriggered(ctx, evt);
                            }
                        }
                    });
                }
            });
            //                    .channel(OioServerSocketChannel.class)
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyEchoServer().server(8001);
    }
}
