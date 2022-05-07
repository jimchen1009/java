package com.jim.demo.netty.Leakdetector;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 检测原理很简单：ReferenceMain
 */
public class LeakDetectorMain {

    private static final ResourceLeakDetector<DetectItem> leakDetector =
            ResourceLeakDetectorFactory.instance().newResourceLeakDetector(DetectItem.class, 10);

    private static final Queue<DetectItemDelegate> queue = new ConcurrentLinkedDeque<>();

    public static void main(String[] args) throws InterruptedException {
        new Thread(LeakDetectorMain::touth).start();
        //问题在哪里，在main函数没有执行之前，leakDetector已经初始化
        //io.netty.leakDetection.level 设置已经是没有意思的了
        //设置 VM options:-Dio.netty.leakDetection.level=PARANOID
        System.setProperty("io.netty.leakDetection.level", "PARANOID");
        for (int i = 0; i < 1000; i++) {
            DetectItemDelegate detectItem = genDetectItem();
            queue.add(detectItem);
            System.out.println("created a item: " + detectItem.toString());
            //这里不会报错多次，reportedLeaks记录调用堆栈，相同一个堆栈只会报错一次
            System.gc();
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private static void touth(){
        while (true){
            DetectItemDelegate detectItem = queue.poll();
            if (detectItem == null){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
            }
            else {
                if (detectItem.touch() < 8) {
                    queue.add(detectItem);
                }
                else {
                    System.out.println("removed a item:" + detectItem.toString());
                }
            }
        }
    }

    private static DetectItemDelegate genDetectItem(){
        DetectItem detectItem = new DetectItem();
        ResourceLeakTracker<DetectItem> tracker = leakDetector.track(detectItem);
        return new DetectItemDelegate(detectItem, tracker);
    }

    private static final class DetectItemDelegate implements IDetectItem{

        private int touchCount = 0;
        private final DetectItem detectItem;
        private final ResourceLeakTracker<DetectItem> tracker;

        public DetectItemDelegate(DetectItem detectItem, ResourceLeakTracker<DetectItem> tracker) {
            this.detectItem = detectItem;
            this.tracker = tracker;
        }

        @Override
        public String toString() {
            return detectItem.toString();
        }

        @Override
        public long uniqueId() {
            return detectItem.uniqueId();
        }

        @Override
        public void close() {
            detectItem.close();
            tracker.close(detectItem);
        }

        public int touch() {
            tracker.record();
            return ++this.touchCount;
        }
    }

    private static final class DetectItem implements IDetectItem{

        private static final AtomicLong idGen = new AtomicLong(0);

        private final long uniqueId;

        public DetectItem() {
            this.uniqueId = idGen.incrementAndGet();
        }

        @Override
        public String toString() {
            return "{" +
                    "uniqueId=" + uniqueId +
                    '}';
        }

        @Override
        public long uniqueId() {
            return uniqueId;
        }

        @Override
        public void close() {

        }
    }

    private interface IDetectItem{

        long uniqueId();

        void close();
    }
}
