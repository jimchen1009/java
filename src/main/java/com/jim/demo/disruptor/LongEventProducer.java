package com.jim.demo.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

public class LongEventProducer {

	private static final Translator TRANSLATOR = new Translator();

	private final RingBuffer<LongEvent> ringBuffer;

	public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}



	public void publishV2(long value) {
		ringBuffer.publishEvent(TRANSLATOR, value);
	}

	public void publish(long value) {
		//可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
		long sequence = ringBuffer.next();
		try {
			//用上面的索引取出一个空的事件用于填充
			LongEvent event = ringBuffer.get(sequence);// for the sequence
			event.setValue(value);
		} finally {
			//发布事件
			ringBuffer.publish(sequence);
		}
		/***
		 * 如果我们使用RingBuffer.next()获取一个事件槽，那么一定要发布对应的事件。
		 * 如果不能发布事件，那么就会引起Disruptor状态的混乱。尤其是在多个事件生产者的情况下会导致事件消费者失速，从而不得不重启应用才能会恢复
		 */
	}


	private static class Translator implements EventTranslatorOneArg<LongEvent, Long> {

		@Override
		public void translateTo(LongEvent event, long sequence, Long arg0) {
			event.setValue(arg0);
		}
	}
}
