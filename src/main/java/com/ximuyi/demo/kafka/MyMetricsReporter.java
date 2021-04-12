package com.ximuyi.demo.kafka;

import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.MetricsReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MyMetricsReporter implements MetricsReporter {

	private static final Logger logger = LoggerFactory.getLogger(MyMetricsReporter.class);

	@Override
	public void init(List<KafkaMetric> metrics) {
		logger.debug("{}", metrics);
	}

	@Override
	public void metricChange(KafkaMetric metric) {
		logger.debug("{}", metric);
	}

	@Override
	public void metricRemoval(KafkaMetric metric) {
		logger.debug("{}", metric);
	}

	@Override
	public void close() {
		logger.debug("");
	}

	@Override
	public void configure(Map<String, ?> configs) {
		logger.debug("{}", configs);
	}
}
