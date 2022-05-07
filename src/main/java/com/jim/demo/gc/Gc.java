package com.jim.demo.gc;

/**
 * Created by chenjingjun on 2018-02-06.
 */
public class Gc {

    public static void main(String[] args) {
        byte[] bytes0 = new byte[11 * 1024 * 1024];
        while (true){
            Thread.yield();
        }
    }
}
