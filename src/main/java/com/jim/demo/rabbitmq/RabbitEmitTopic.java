package com.jim.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RabbitEmitTopic {

	private static final Logger logger = LoggerFactory.getLogger(RabbitEmitTopic.class);

	private static final String EXCHANGE_NAME = "topic_logs";

	public static void main(String[] argv) throws Exception {
		try (Connection connection = MyConnectionFactory.newConnection();
			 Channel channel = connection.createChannel()) {

			channel.exchangeDeclare(EXCHANGE_NAME, "topic");

			for (int i = 0; i < 100000; i++) {
				String routingKey = TopicRoutingKey.randomOne(false);
				String message = String.format("%s[%s]", routingKey, i);
				channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
				logger.debug("Sent '" + message + "'");
				TimeUnit.MILLISECONDS.sleep(500);
			}
		}
	}
}
