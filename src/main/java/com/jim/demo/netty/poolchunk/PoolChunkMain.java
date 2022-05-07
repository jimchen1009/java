package com.jim.demo.netty.poolchunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class PoolChunkMain {
    public static void main(String[] args){
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(1024);
    }
}
