package com.jim.demo.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InterruptedThreadMain {
    private static final Logger logger = LoggerFactory.getLogger(DeathLockedThread.class);

    public static class InterruptedThread extends Thread {

        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("InterruptedThread running...");
                try {
                    /*
                     * 如果线程阻塞，将不会去检查中断信号量stop变量，所 以thread.interrupt()
                     * 会使阻塞线程从阻塞的地方抛出异常，让阻塞线程从阻塞状态逃离出来，并
                     * 进行异常块进行 相应的处理
                     */
                    Thread.sleep(1000);// 线程阻塞，如果线程收到中断操作信号将抛出异常
                } catch (InterruptedException e) {
                    System.out.println("InterruptedThread interrupted...");
                    /*
                     * 如果线程在调用 Object.wait()方法，或者该类的 join() 、sleep()方法
                     * 过程中受阻，则其中断状态将被清除
                     */
                    System.out.println(this.isInterrupted());// false

                    //中不中断由自己决定，如果需要真真中断线程，则需要重新设置中断位，如果
                    //不需要，则不用调用
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("InterruptedThread exiting under request...");
        }
    }

    public static class DeathLockedThread extends Thread {

        private final Lock lock1;
        private final Lock lock2;

        public DeathLockedThread(Lock lock1, Lock lock2) {
            this.lock1 = lock1;
            this.lock2 = lock2;
        }

        public void run() {
            while (true){
                lock();
                lockInterruptibly();
            }
        }

        private void lock() {
            try {
                synchronized (lock1) {
                    Thread.sleep(1000);// 不会在这里死掉
                    synchronized (lock2) {// 会锁在这里，虽然阻塞了，但不会抛异常
                        System.out.println(Thread.currentThread());
                    }
                }
            } catch (InterruptedException e) {
                logger.error("", e);
            }
        }

        private void lockInterruptibly() {
            try {
                lock1.lockInterruptibly();
                Thread.sleep(100);// 不会在这里死掉
                lock2.lockInterruptibly();
            } catch (InterruptedException e) {
                logger.error("", e);
            }
            logger.debug("lockInterruptibly: " + Thread.currentThread());
        }
    }

    public  static class ChennelInterruptedThread extends Thread {
        public volatile ServerSocket socket;

        public void run() {
            try {
                socket = new ServerSocket(8888);
            } catch (IOException e) {
                logger.error("Could not create the socket...", e);
                return;
            }
            while (!Thread.currentThread().isInterrupted()) {
                logger.debug("Waiting for connection...");
                try {
                    socket.accept();
                } catch (IOException e) {
                    logger.debug("accept() failed or interrupted...");
                    Thread.currentThread().interrupt();//重新设置中断标示位
                }
            }
            System.out.println("Thread exiting under request...");
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        testInterrupted();
//        testDealthLockThreadInterrupted();
//        testChannelInterrupted();
    }

    private static void testChannelInterrupted() throws InterruptedException, IOException {
        ChennelInterruptedThread thread = new ChennelInterruptedThread();
        System.out.println("Starting thread...");
        thread.start();
        Thread.sleep(3000);
        System.out.println("Asking thread to stop...");
        Thread.currentThread().interrupt();// 再调用interrupt方法
        thread.socket.close();// 再调用close方法
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
        System.out.println("Stopping application...");
    }

    private static void testInterrupted()  throws InterruptedException{
        InterruptedThread thread = new InterruptedThread();
        System.out.println("Starting thread...");
        thread.start();
        Thread.sleep(3000);
        System.out.println("Asking thread to stop...");
        thread.interrupt();// 等中断信号量设置后再调用
        Thread.sleep(3000);
        System.out.println("Stopping application...");
    }

    private static void testDealthLockThreadInterrupted()  throws InterruptedException{
        final Lock lock1 = new ReentrantLock();
        final Lock lock2 = new ReentrantLock();
        Thread thread1 = new DeathLockedThread(lock1, lock2);
        Thread thread2 = new DeathLockedThread(lock2, lock1);
        System.out.println("Starting thread...");
        int count = 1000;
        thread1.start();
        thread2.start();
        while (count-- > 0 ){
            Thread.sleep(5000);
            new Thread(()->{
                System.out.println("Interrupting thread...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                thread1.interrupt();
                thread2.interrupt();
            }).start();
        }
        System.out.println("Stopping application...");
    }
}
