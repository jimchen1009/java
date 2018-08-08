package com.ximuyi.demo.c3p0;

import com.mchange.v2.async.ThreadPoolAsynchronousRunner;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by chenjingjun on 2018-01-31.
 */
public class C3p0 {

    /**
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        deadlock();
    }

    public static void deadlock() throws InterruptedException {
        LinkedList list = new LinkedList();
        synchronized (list){
            if(Thread.holdsLock(list)){
                System.out.println("Thread.holdsLock");
            }
        }
        LinkedList cpmpare = new LinkedList();
        for (int i = 0; i < 2; i++){
            list.addLast(new DeadLock(i));
            cpmpare.addLast(new DeadLock(i));
        }
        System.out.println("cpmpare.equals(list)==" + cpmpare.equals(list));
        System.out.println("cpmpare.clone().equals(list)==" + cpmpare.clone().equals(list));
        ThreadPoolAsynchronousRunner pool = new ThreadPoolAsynchronousRunner(5, false, 0 , 1, 10);
        int i = 1;
        while (true){
            pool.postRunnable(new DeadLock(i));
        }
    }

    static class DeadLock implements Runnable{

        private final int i;

        public DeadLock(int i) {
            this.i = i;
        }

        @Override
        public void run() {
//            System.out.println("-----------------:" + i);
        }


        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }
}
