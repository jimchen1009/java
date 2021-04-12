package com.ximuyi.demo.kafka;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MyProducerInterceptor implements ProducerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(MyProducerInterceptor.class);


	@Override
	public ProducerRecord onSend(ProducerRecord record) {
		logger.debug("{}", record);
		return record;
	}

	@Override
	public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
		logger.debug("{}", metadata, exception);
	}

	@Override
	public void close() {
		logger.debug("");
		System.out.println("[MyProducerInterceptor.close]");
	}

	@Override
	public void configure(Map<String, ?> configs) {
		logger.debug("{}", configs);
	}
}
