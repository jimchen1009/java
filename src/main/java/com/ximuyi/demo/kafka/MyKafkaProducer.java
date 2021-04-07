package com.ximuyi.demo.kafka;

import com.ximuyi.common.PropertiesUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MyKafkaProducer<K, V> {

	private final String topic;
	private final Producer<K, V> producer;

	public MyKafkaProducer(String topic, Properties properties) {
		this.topic = topic;
		this.producer = new KafkaProducer<>(properties);
	}

	public void send(K key, V value){
		producer.send(new MyProducerRecord<>(topic, key, value));
	}

	public void close(){
		producer.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Properties properties = PropertiesUtil.getResourceAsStream("kafka/producer.properties");
		MyKafkaProducer<String, String> kafkaProducer = new MyKafkaProducer<>("Jim", properties);
		for (int i = 0; i < 1000; i++) {
			kafkaProducer.send(Integer.toString(i), Integer.toString(i));
			TimeUnit.SECONDS.sleep(30);
		}
		kafkaProducer.close();
	}
}
