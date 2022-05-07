package com.jim.demo.metrics;


import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.codahale.metrics.jmx.JmxReporter;
import com.jim.common.ResourceUtil;
import com.jim.demo.mysql.MysqlSourceBuilder;
import com.zaxxer.hikari.HikariDataSource;
import jodd.util.ThreadUtil;
import org.apache.commons.lang3.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayDeque;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.TimeUnit;


public class DropwizardMetric {

	public static void main(String args[]) throws IOException {
		final MetricRegistry registry = new MetricRegistry();

		ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(registry)
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.build();
		consoleReporter.start(5, TimeUnit.SECONDS);

		CsvReporter csvReporter = CsvReporter.forRegistry(registry)
				.formatFor(Locale.US)
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.build(new File("C:/Users/chenjingjun/Desktop/demo/"));
		csvReporter.start(1, TimeUnit.SECONDS);

		JmxReporter jmxReporter = JmxReporter.forRegistry(registry).build();
		jmxReporter.start();

		Meter requests = registry.meter("requests");
		requests.mark();

		Queue<Integer> queue = new ArrayDeque<>();
		registry.register("gauge", (Gauge<Integer>) queue::size);

		Counter counter = registry.counter("counter");
		counter.inc();

		Histogram histogram = registry.histogram("histogram");
		for (int i = 0; i < 10; i++) {
			histogram.update(RandomUtils.nextInt(0, 100));
		}

		/**
		 * This timer will measure the amount of time it takes to process each request in nanoseconds and provide a rate of requests in requests per second
		 */
		Timer timer = registry.timer("timer");
		Timer.Context context = timer.time();
		context.stop();




		HealthCheckRegistry healthRegistry = new HealthCheckRegistry();
		Properties properties = ResourceUtil.getResourceAsProperties("demo.properties");
		HikariDataSource dataSource = new MysqlSourceBuilder(properties).build(2);
		healthRegistry.register("dataSource", new DatabaseHealthCheck(dataSource));
		healthRegistry.register("threadDeadlock", new ThreadDeadlockHealthCheck());

		final Map<String, HealthCheck.Result> results = healthRegistry.runHealthChecks();
		for (Map.Entry<String, HealthCheck.Result> entry : results.entrySet()) {
			if (entry.getValue().isHealthy()) {
				System.out.println(entry.getKey() + " is healthy");
			}
			else {
				System.err.println(entry.getKey() + " is UNHEALTHY: " + entry.getValue().getMessage());
				final Throwable e = entry.getValue().getError();
				if (e != null) {
					e.printStackTrace();
				}
			}
		}

		ThreadUtil.sleep(TimeUnit.MINUTES.toMillis(5));
	}


	private static class DatabaseHealthCheck extends HealthCheck {

		private final HikariDataSource dataSource;

		public DatabaseHealthCheck(HikariDataSource dataSource) {
			this.dataSource = dataSource;
		}

		@Override
		public HealthCheck.Result check() throws Exception {
			Connection connection = dataSource.getConnection();
			try {
				PreparedStatement statement = connection.prepareStatement("SELECT 1");
				if (statement.execute()) {
					return HealthCheck.Result.healthy();
				}
				else {
					return HealthCheck.Result.unhealthy("statement.execute() failed");
				}
			}
			catch (Throwable throwable){
				return HealthCheck.Result.unhealthy(throwable);
			}
			finally {
				connection.close();
			}
		}
	}
}
