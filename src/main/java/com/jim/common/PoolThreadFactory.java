package com.jim.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class PoolThreadFactory implements ThreadFactory {

	private static final Logger logger = LoggerFactory.getLogger(PoolThreadFactory.class);

	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(0);
	private final String namePrefix;
	private final Boolean daemon;

	public PoolThreadFactory(String namePrefix) {
		// daemon随创建线程
		this(namePrefix, null);
	}

	public PoolThreadFactory(String namePrefix, Boolean daemon) {
		SecurityManager s = System.getSecurityManager();
		this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix = namePrefix;
		this.daemon = daemon;
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix + "-" + threadNumber.getAndIncrement(), 0);
		// The newly created thread is initially marked as being a daemon thread if and only if
		// the thread creating it is currently marked as a daemon thread.
		if (this.daemon != null) {
			t.setDaemon(this.daemon.booleanValue());
		}

		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}

		return t;
	}

	/**
	 *
	 * @param consumer
	 * @param timeUnit
	 * @param duration
	 * @param endCount
	 * @return
	 */
	public Thread newLoopThread(Consumer<Integer> consumer, TimeUnit timeUnit, int duration, int endCount) {
		AtomicInteger counter = new AtomicInteger(0);
		Runnable runnable = ()-> {
			while(endCount <= 0 || counter.get() < endCount){
				int count = counter.incrementAndGet();
				try {
					if (duration > 0){
						long current = System.currentTimeMillis();
						long expiration = timeUnit.toMillis(duration) + current;
						boolean isInterrupted = Thread.currentThread().isInterrupted();
						while(current < expiration){
							try {
								Thread.sleep(expiration - current);
							}
							catch (InterruptedException e){
								if (isInterrupted){
								}
								else {
									Thread.interrupted();
								}
								logger.error("", e);
							}
							current = System.currentTimeMillis();
						}
					}
					consumer.accept(count);
				}
				catch (Throwable t){
					logger.error("runnable:{} error", count, t);
				}
			}
		};
		return newThread(runnable);
	}
}