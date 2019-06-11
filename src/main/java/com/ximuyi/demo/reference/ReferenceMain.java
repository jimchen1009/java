package com.ximuyi.demo.reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 当一个Reference的referent被回收时，垃圾回收器会把reference添加到pending这个链表里
 * 然后Reference-handler thread不断的读取pending中的reference，把它加入到对应的ReferenceQueue中
 *
 *
 * 1. JVM会根据当前内存的情况来决定是否回收softly-reachable对象，但只要referent有强引用存在，
 * 该referent就一定不会被清理，因此SoftReference适合用来实现memory-sensitive caches。
 *
 *
 * 2. 当一个对象被WeakReference引用时，处于weakly-reachable状态时，只要发生GC时，就会被清除，
 * 同时会把WeakReference注册到引用队列中(如果存在的话)。
 *
 *
 * 3. PhantomReference主要作为其指向的referent被回收时的一种通知机制,它就是利用上文讲到的ReferenceQueue实现的。
 * 当referent被gc回收时，JVM自动把PhantomReference对象(reference)本身加入到ReferenceQueue中，
 * 像发出信号通知一样，表明该reference指向的referent被回收。然后可以通过去queue中取到reference，此时说明其指向的referent已经被回收，
 * 可以通过这个通知机制来做额外的清场工作。 因此有些情况可以用PhantomReference 代替finalize()，做资源释放更明智。
 *
 *
 * 4. FinalReference 引用类型主要是为虚拟机提供的，提供 对象被gc前需要执行finalize方法的对象 的机制。
 * FinalReference 很简单就是extend Reference类，没有做其他逻辑，只是把访问权限改为package,因此我们是无法直接使用的。
 * 直接看：Finalizer
 */
public class ReferenceMain {
	private static final Logger logger = LoggerFactory.getLogger(ReferenceMain.class);

    private static final ReferenceQueue<TestBytes> queue = new ReferenceQueue<>();
    private static List<Reference<TestBytes>> root = new ArrayList<>();
    private static ReferenceThread thread = new ReferenceThread(ReferenceMain::onReferenceCollected);

    public static void main(String[] args) throws Exception {
        thread.start();
        for (int i = 0; i < 1000; i++) {
            TestBytes bytes = add(i);
            System.out.println("produced one and related reference's address: " + bytes.getAddress());
            System.gc();
            TimeUnit.SECONDS.sleep(5);
        }
    }

    private static void onReferenceCollected(){
        while (true) {
            try {
                //这个queue需要remove，不然会一直膨胀
                Reference<? extends TestBytes> reference = queue.remove();
                String address = Integer.toHexString(reference.hashCode());
                String format = String.format("collected: the address of its reference is: %s\n", address);
	            logger.debug(format);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private static final TestBytes add(int index) throws Exception {
        TestBytes bytes = new TestBytes(index);
//        Reference reference = new SoftReference<>(referenctBytes, queue);
        Reference<TestBytes> reference = new WeakReference<>(bytes, queue);
//        Reference reference = new PhantomReference<>(referenctBytes, queue);
        bytes.setAddress(Integer.toHexString(reference.hashCode()));
        root.add(reference);
        return bytes;
    }

    private static class TestBytes {

        private static final int SIZE = 1024 * 1024;

        private final int index;
        private final byte[] bytes;
        private String address;           //Reference地址

        public TestBytes(int index) {
            this.index = index;
            this.bytes = new byte[SIZE];
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "{" +
                    "index=" + index +
                    "address=" + address +
                    ", bytes.lenth=" + bytes.length +
                    '}';
        }
    }

    private static class ReferenceThread extends Thread{

        public ReferenceThread(Runnable runnable) {
            super(runnable);
        }
    }
}
