package com.ximuyi.demo.kafka;

import com.ximuyi.common.PropertiesUtil;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyKafkaConsumer<K, V> implements ConsumerRebalanceListener {

	private final Collection<String> topicLis;
	private final KafkaConsumer<K, V> consumer;
	private final AtomicBoolean isRunning;

	public MyKafkaConsumer(Collection<String> topicLis, Properties properties) {
		this.topicLis = topicLis;
		this.consumer = new KafkaConsumer<>(properties);
		this.isRunning = new AtomicBoolean(false);
	}

	public void start(){
		if (isRunning.compareAndSet(false, true)) {
			consumer.subscribe(topicLis, this);
		}
	}

	public void stop(){
		consumer.close();
	}


	@Override
	public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
		System.out.println("[onPartitionsRevoked]: " + partitions);
	}

	@Override
	public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
		System.out.println("[onPartitionsAssigned]: " + partitions);
	}

	@Override
	public void onPartitionsLost(Collection<TopicPartition> partitions) {
		System.out.println("[onPartitionsLost]: " + partitions);
	}

	public Collection<MyConsumerRecord> pool(){
		/**
		 * Note: Using automatic offset commits can also give you "at-least-once" delivery,
		 * but the requirement is that you must consume all data returned from each call to poll(Duration) before any subsequent calls, or before closing the consumer.
		 * If you fail to do either of these, it is possible for the committed offset to get ahead of the consumed position, which results in missing records.
		 * The advantage of using manual offset control is that you have direct control over when a record is considered "consumed."
		 */
		ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(100));
		if (records.isEmpty()) {
			return Collections.emptyList();
		}
		Set<TopicPartition> partitions = records.partitions();
		Map<TopicPartition, OffsetAndMetadata> partitionOffsetMap = new HashMap<>();
		List<MyConsumerRecord> myConsumerRecordList = new ArrayList<>();
		for (TopicPartition partition : partitions) {
			List<ConsumerRecord<K, V>> recordList = records.records(partition);
			Optional<ConsumerRecord<K, V>> optional = recordList.stream().max(Comparator.comparingLong(ConsumerRecord::offset));
			optional.ifPresent(kvConsumerRecord -> partitionOffsetMap.put(partition, new OffsetAndMetadata(kvConsumerRecord.offset() + 1)));
			for (ConsumerRecord<K, V> record : recordList) {
				myConsumerRecordList.add(new MyConsumerRecord<>(record));
			}
		}
		consumer.commitSync(partitionOffsetMap);
		return myConsumerRecordList;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Properties properties = PropertiesUtil.getResourceAsStream("kafka/consumer.properties");
		MyKafkaConsumer<String, String> kafkaConsumer = new MyKafkaConsumer<>(Collections.singleton("Jim"), properties);
		kafkaConsumer.start();
		for (int i = 0; i < 1000; i++) {
			Collection<MyConsumerRecord> consumerRecords = kafkaConsumer.pool();
			for (MyConsumerRecord myConsumerRecord : consumerRecords) {
				System.out.println(myConsumerRecord);
			}
			TimeUnit.SECONDS.sleep(30);
		}
		kafkaConsumer.stop();
	}
}
