package com.ximuyi.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RabbitSend {

	private static final Logger logger = LoggerFactory.getLogger(RabbitSend.class);

	private final static String QUEUE_NAME = "task_queue";

	public static void main(String[] argv) throws Exception {
		try (Connection connection = MyConnectionFactory.newConnection(); Channel channel = connection.createChannel()) {
			/***
			 * Two things are required to make sure that messages aren't lost:
			 * we need to mark both the queue and messages as durable.
			 */
			boolean durable = true;
			channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
			for (int i = 0; i < 1000; i++) {
				String message = "Hello World-" + i + ".";
				/***
				 * 1. Now we need to mark our messages as persistent:
				 * MessageProperties.PERSISTENT_TEXT_PLAIN
				 * 2. we knew nothing about exchanges, but still were able to send messages to queues.
				 * That was possible because we were using a default exchange, which we identify by the empty string ("").
				 */
				channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
				logger.debug(" [x] Sent '" + message + "'");
				TimeUnit.SECONDS.sleep(1);
			}
		}
	}
}
