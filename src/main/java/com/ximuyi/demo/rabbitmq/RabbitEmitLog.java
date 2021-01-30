package com.ximuyi.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RabbitEmitLog {
	private static final Logger logger = LoggerFactory.getLogger(RabbitEmitLog.class);

	private static final String EXCHANGE_NAME = "logs";

	public static void main(String[] argv) throws Exception {
		try (Connection connection = MyConnectionFactory.newConnection(); Channel channel = connection.createChannel()) {
			/**
			 * As you see, after establishing the connection we declared the exchange.
			 * This step is necessary as publishing to a non-existing exchange is forbidden.
			 */
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

			for (int i = 0; i < 1000000; i++) {
				String message = "emit log-" + i;
				/**
				 * The messages will be lost if no queue is bound to the exchange yet
				 * We need to supply a routingKey when sending, but its value is ignored for fanout exchanges.
				 */
				channel.basicPublish(EXCHANGE_NAME, i %2 == 0 ? "" : "A", null, message.getBytes("UTF-8"));
				logger.debug("Sent '" + message + "'");
				TimeUnit.SECONDS.sleep(2);
			}
		}
	}
}
