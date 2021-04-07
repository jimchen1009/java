package com.ximuyi.demo.kafka;

import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.MetricsReporter;

import java.util.List;
import java.util.Map;

public class MyMetricsReporter implements MetricsReporter {

	@Override
	public void init(List<KafkaMetric> metrics) {
		System.out.println("[MyMetricsReporter.init]" + metrics);
	}

	@Override
	public void metricChange(KafkaMetric metric) {
		System.out.println("[MyMetricsReporter.metricChange]" + metric);
	}

	@Override
	public void metricRemoval(KafkaMetric metric) {
		System.out.println("[MyMetricsReporter.metricRemoval]" + metric);
	}

	@Override
	public void close() {
		System.out.println("[MyMetricsReporter.close]");
	}

	@Override
	public void configure(Map<String, ?> configs) {
		System.out.println("[MyMetricsReporter.configure]" + configs);
	}
}
