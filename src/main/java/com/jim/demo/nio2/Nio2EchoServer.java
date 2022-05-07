package com.jim.demo.nio2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class Nio2EchoServer {

    private static final Logger logger = LoggerFactory.getLogger(Nio2EchoServer.class);

    public void server(int port) throws IOException {

        AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(port);
        serverChannel.bind(address);

        final CountDownLatch downLatch = new CountDownLatch(10);

        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>(){

            @Override
            public void completed(AsynchronousSocketChannel channel, Object attachment) {
                logger.debug("appected channle: {}", channel);
                serverChannel.accept(null, this);
                ByteBuffer buffer = ByteBuffer.allocate(50);
                channel.read(buffer, buffer, new EchoCompletionHandler(channel));
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                try {
                    serverChannel.close();
                } catch (IOException e) {// ingnore on close
                } finally {
                    downLatch.countDown();
                }
            }
        });

        logger.debug("server is running...");
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //???
            logger.error("", e);
        }
        logger.debug("server is running...");
    }


    public static class EchoCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

        private final AsynchronousSocketChannel channel;

        public EchoCompletionHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            if (attachment.remaining() > 0){
                channel.read(attachment, attachment, EchoCompletionHandler.this);
                return;
            }
            byte[] array = attachment.array();
            String string = new String(array);
            logger.debug("channel[{}] read array={}", channel, string);

            attachment.flip();
            channel.write(attachment, attachment, new CompletionHandler<Integer, ByteBuffer>(){

                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    attachment.compact();
                    channel.read(attachment, attachment, EchoCompletionHandler.this);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        // ingnore on close
                        logger.error("", e);
                    }
                }
            });
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            logger.error("", exc);
        }
    }


    public static void main(String[] args) throws IOException {
        new Nio2EchoServer().server(8001);
    }
}
