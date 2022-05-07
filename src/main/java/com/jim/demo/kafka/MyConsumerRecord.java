package com.jim.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class MyConsumerRecord<K, V>  {

	private final ConsumerRecord<K, V> consumerRecord;

	public MyConsumerRecord(ConsumerRecord<K, V> consumerRecords) {
		this.consumerRecord = consumerRecords;
	}

	public K key() {
		return consumerRecord.key();
	}

	public V value() {
		return consumerRecord.value();
	}

	@Override
	public String toString() {
		return consumerRecord.toString();
	}
}
