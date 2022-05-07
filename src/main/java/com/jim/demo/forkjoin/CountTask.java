package com.jim.demo.forkjoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * Created by chenjingjun on 2017-11-24.
 */
public class CountTask extends RecursiveTask<Integer> {
    private static final int SHOLD = 2;

    private final long start ;
    private final long end;

    public CountTask(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        boolean compute = (end - start) <= SHOLD;
        int sum = 0;
        if (compute){
            for (long i = start; i < end; i++){
                sum += i;
            }
        }
        else {
            long middle = (end + start) / 2;
            CountTask left = new CountTask(start,  middle);
            left.fork();
            int sum0 = left.join();
            if (left.isCompletedAbnormally()){
                left.getException().printStackTrace();
            }

            CountTask right = new CountTask(middle + 1, end);
            right.fork();
            int sum1 = right.join();
            if (right.isCompletedAbnormally()){
                right.getException().printStackTrace();
            }
            sum = ( sum1+ sum0);
        }
        return sum;
    }


    private static final Logger logger = LoggerFactory.getLogger(CountTask.class);

    public static void main(String[] args){
        ExecutorService service = Executors.newWorkStealingPool();
        for (int i = 0; i < 100; i++) {
            String valueOf = String.valueOf(i);
            service.submit( ()-> logger.debug("service: {}", valueOf));
        }
        ForkJoinPool pool = new ForkJoinPool();
        CountTask task = new CountTask(1, 50000);
        Future<Integer> future = pool.submit(task);
        try {
            int sum = future.get();
            System.out.println(sum);
        }
        catch (Throwable t){
            t.printStackTrace();
        }
    }
}
