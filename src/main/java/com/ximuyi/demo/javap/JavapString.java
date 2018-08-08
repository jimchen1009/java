package com.ximuyi.demo.javap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavapString {

    private boolean enable;

    public JavapString(boolean enable) {
        this.enable = enable;
    }

    public void dispatch(Runnable runnable){
        if (enable){
            runnable.run();
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int i = 10;
        String name = "name";
        Javap javap = new Javap(name, i);
        executor.execute(() -> System.out.println(javap.getString()));
//        JavapString obj = new JavapString(true);
//        obj.dispatch(() -> System.out.println(javap.getString()));
//        obj.dispatch(() -> System.out.println(name + i));
    }

    public class Cls{

    }
}
