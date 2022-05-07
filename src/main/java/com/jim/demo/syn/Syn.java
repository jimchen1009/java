package com.jim.demo.syn;

import java.util.concurrent.TimeUnit;

/**
 * Created by chenjingjun on 2018-02-01.
 */
public class Syn implements Runnable {


    public Syn() {
        Thread t = new Thread(()->syn0(), "OWN");
        t.start();
    }

    public synchronized void syn0() {
        System.out.println(Thread.currentThread().getName() + " hold lock:" + Thread.holdsLock(this));
        while (true){
            Thread.yield();
        }
    }

    public synchronized void syn1() {
        System.out.println(Thread.currentThread().getName() + " hold lock:" + Thread.holdsLock(this));
        System.out.println(Thread.currentThread().getName() + " ready release lock." );
    }

    @Override
    public  void run() {
        syn1();
    }

    public static void main(String[] args) throws InterruptedException {
        Syn sync = new Syn();
        TimeUnit.SECONDS.sleep(1);
        Thread t = new Thread(sync, "MAIN");
        //启动后调用f()方法,无法获取当前实例锁处于等待状态
        t.start();
        TimeUnit.SECONDS.sleep(1);
        //中断线程,无法生效
        t.interrupt();
    }
}
