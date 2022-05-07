package com.jim.demo.netty.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.ibatis.io.Resources;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

public class LogEventBroadcaster {

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
        this.file = file;
    }

    public void run() throws IOException {
        Channel ch = bootstrap.bind(0).syncUninterruptibly().channel();
        long pointer = 0;
        for (;;) {
            long len = file.length();
            if (len < pointer) {
                // file was reset
                pointer = len;
            } else if (len > pointer) {
                // Content was added
                RandomAccessFile randomfile = new RandomAccessFile(file, "r");
                randomfile.seek(pointer);
                String line;
                while ((line = randomfile.readLine()) != null) {
                    ch.write(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }
                ch.flush();
                pointer = randomfile.getFilePointer();
                randomfile.close();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        File file = Resources.getResourceAsFile("common.txt");
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", 8001), file);
        try {
            broadcaster.run();
        } finally {
            broadcaster.stop();
        }
    }
}
