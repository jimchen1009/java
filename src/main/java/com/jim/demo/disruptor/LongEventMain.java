package com.jim.demo.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class LongEventMain {

	/***
	 * 下面的例子，每一个handler就会创建一个线程：BatchEventProcessor【离线的run是一个for循环】
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		EventFactory<LongEvent> eventFactory = new LongEventFactory();
		//PoolThreadFactory threadFactory = new PoolThreadFactory("disruptor");
		ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
		int ringBufferSize = 1024 * 1024; // RingBuffer 大小，必须是 2 的 N 次方；

		/**
		 * BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现；
		 * SleepingWaitStrategy 的性能表现跟 BlockingWaitStrategy 差不多，对 CPU 的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景；
		 * YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于 CPU 逻辑核心数的场景中，推荐使用此策略；例如，CPU开启超线程的特性。
		 */
		Disruptor<LongEvent> disruptor = new Disruptor<>(eventFactory, ringBufferSize, threadFactory, ProducerType.MULTI, new BlockingWaitStrategy());
		LongEventHandler[] handlers =new LongEventHandler[]{
				new LongEventHandler("Jim"),
				new LongEventHandler("Cat"),
				new LongEventHandler("Joce"),
				new LongEventHandler("Mike")
		};
		//类型1：
		EventHandlerGroup<LongEvent> handlerGroup = null;
		for (LongEventHandler handler : handlers) {
			if (handlerGroup == null){
				handlerGroup = disruptor.handleEventsWith(handler);
			}
			else {
				handlerGroup = handlerGroup.then(handler);
			}
		}
		//类型2:pool里面随机一个handler处理一个事件
		//disruptor.handleEventsWithWorkerPool(handlers);


		disruptor.start();


		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
		LongEventProducer producer = new LongEventProducer(ringBuffer);

		AtomicLong atomicLong = new AtomicLong();
		for (int i = 0; i < 1; i++) {
			Thread thread = new Thread(() -> {
				while(true){
					try {
						producer.publishV2(atomicLong.incrementAndGet());
						TimeUnit.SECONDS.sleep(10);
					}
					catch (Throwable ignored){
					}
				}
			});
			thread.start();
		}

		TimeUnit.HOURS.sleep(1);
	}
}
