package com.ximuyi.demo.syn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chenjingjun on 2018-03-29.
 */
public class Lock {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        for (int j =0; j < list.size(); j++){
            System.out.println(j);
        }
        ReentrantLock rock = new ReentrantLock();
        new Thread(()->{
            rock.lock();
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
            }
        }).start();
        TimeUnit.SECONDS.sleep(5);
        rock.lock();
    }
}
