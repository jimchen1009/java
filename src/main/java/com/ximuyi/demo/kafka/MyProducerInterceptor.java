package com.ximuyi.demo.kafka;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

public class MyProducerInterceptor implements ProducerInterceptor {

	@Override
	public ProducerRecord onSend(ProducerRecord record) {
		System.out.println("[MyProducerInterceptor.onSend]" + record);
		return record;
	}

	@Override
	public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
		System.out.println("[MyProducerInterceptor.onAcknowledgement]" + metadata);
	}

	@Override
	public void close() {
		System.out.println("[MyProducerInterceptor.close]");
	}

	@Override
	public void configure(Map<String, ?> configs) {
		System.out.println("[MyProducerInterceptor.configure]" + configs);
	}
}
