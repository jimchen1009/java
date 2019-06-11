package com.ximuyi.demo.java.cyclicbarrier;

import com.ximuyi.common.PoolThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class CyclicBarrierMain {

	private static final Thread.UncaughtExceptionHandler excpetionHandler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("{}", t.getName(), e);
		}
	};

	private static final Logger logger = LoggerFactory.getLogger(CyclicBarrierMain.class);

	public static void main(String[] args) throws InterruptedException {
		PoolThreadFactory factory = new PoolThreadFactory("CyclicBarrier");
		List<Thread> threadList = new ArrayList<>();
		final int jobCount = 2;
		AtomicLong atomicLong = new AtomicLong();
		CyclicBarrier barrier = new CyclicBarrier(jobCount, ()->{
			logger.debug("---finished count:{}", atomicLong.incrementAndGet());
		});
		for (int i = 0; i < jobCount; i++) {
			Thread thread = factory.newThread(new Job(i, barrier));
			threadList.add(thread);
			thread.setUncaughtExceptionHandler(excpetionHandler);
			thread.start();
		}
		while(true){
			TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10, 20));
			int index = ThreadLocalRandom.current().nextInt(threadList.size());
			logger.debug("thread({}).interrupt()", index);
			Thread thread = threadList.get(index);
			thread.interrupt();
		}
	}

	private static final class Job implements Runnable{

		private final int index;
		private final CyclicBarrier barrier ;

		public Job(int index, CyclicBarrier barrier) {
			this.index = index;
			this.barrier = barrier;
		}

		@Override
		public void run() {
			while(true){
				try {
					logger.debug("Job-{} is executing...", index);
					TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 10));
					barrier.await();
					logger.debug("Job-{} finished executing.", index);
				} catch (InterruptedException e) {
					boolean interrupted = Thread.interrupted();
					logger.error("Job-{} InterruptedException interrupted:{}", index, interrupted);
				} catch (BrokenBarrierException e) {
					logger.error("Job-{} BrokenBarrierException", index);
				}
			}
		}
	}
}
