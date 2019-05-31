package com.ximuyi.demo.disruptor;

import com.lmax.disruptor.BatchStartAware;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.TimeoutHandler;
import com.lmax.disruptor.WorkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongEventHandler implements EventHandler<LongEvent>, WorkHandler<LongEvent>, LifecycleAware, TimeoutHandler, BatchStartAware {

	private static final Logger logger = LoggerFactory.getLogger(LongEventHandler.class);
	private final String name;


	public LongEventHandler(String name) {
		this.name = name;
	}

	@Override
	public void onEvent(LongEvent event) throws Exception {
		event.finish(name);
		String string = event.toString();
		logger.debug("[{}]{}: {}", Thread.currentThread().getName(), name, string);
	}

	@Override
	public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {
		event.finish(name);
		String string = event.toString();
		logger.debug("[{}]{}: {}", Thread.currentThread().getName(), name, string);
	}

	@Override
	public void onStart() {
		logger.debug("[{}]{}: {}", Thread.currentThread().getName(), name, "start");
	}

	@Override
	public void onShutdown() {
		logger.debug("[{}]{}: {}", Thread.currentThread().getName(), name, "shutdown");
	}

	@Override
	public void onTimeout(long sequence) throws Exception {
		logger.debug("[{}]{}:{} {}", Thread.currentThread().getName(), name, sequence, "timeout");
	}

	@Override
	public void onBatchStart(long batchSize) {
		logger.debug("[{}]{}:{} {}", Thread.currentThread().getName(), name, "batch start", batchSize);
	}
}
