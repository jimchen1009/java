package com.ximuyi.demo.netty.wheeltimer;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class WheelTimerMain {
    private static final Logger logger = LoggerFactory.getLogger(WheelTimerMain.class);

    /***
     *
     */
    private static HashedWheelTimer timer = new HashedWheelTimer(1, TimeUnit.SECONDS, 60);

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(WheelTimerMain::schedule);
        thread.start();
        thread.join();
    }


    private static void schedule() {
        for ( ; ; ) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            }
            catch (InterruptedException e) {
            }
            WheelTimerTask timerTask = new WheelTimerTask(1, TimeUnit.SECONDS);
            timer.newTimeout(timerTask, timerTask.delay, timerTask.timeUnit);
        }
    }

    private static class WheelTimerTask implements TimerTask {

        private static AtomicLong atomicLong = new AtomicLong(0);

        private final long delay;
        private final TimeUnit timeUnit;

        private final long index;
        private final long addDatetime;

        public WheelTimerTask(long delay, TimeUnit timeUnit) {
            this.delay = delay;
            this.timeUnit = timeUnit;
            this.index = atomicLong.incrementAndGet();
            this.addDatetime = System.currentTimeMillis();
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            logger.debug("index:{}, duration:{}", index , System.currentTimeMillis() - addDatetime);
        }
    }

    private static class WheelTimerFuture{

    }
}
