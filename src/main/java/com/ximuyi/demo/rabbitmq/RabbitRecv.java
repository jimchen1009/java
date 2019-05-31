package com.ximuyi.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RabbitRecv {
	private static final Logger logger = LoggerFactory.getLogger(RabbitRecv.class);

	private final static String QUEUE_NAME = "task_queue";

	public static void main(String[] argv) throws Exception {
		Connection connection = MyConectionFactory.newConnection();
		Channel channel = connection.createChannel();
		boolean durable = true;
		channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
		logger.debug("Waiting for messages. To exit press CTRL+C");
		channel.basicQos(1);

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");
			logger.debug("Received '" + message + "'");
			try {
				/***
				 * By default, RabbitMQ will send each message to the next consumer, in sequence.
				 * On average every consumer will get the same number of messages.
				 * 默认情况下：一个消费者会卡主其他消费者~
				 */
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException ignored) {

			}
			finally {
				logger.debug("Done");
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				/**
				 * basicReject: provides no support for negatively acknowledging messages in bulk.
				 */
				//channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
				//channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false,true);

			}
		};
		boolean autoAck = false;
		channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
	}
}
