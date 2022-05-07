package com.jim.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RabbitReceiveTopic {
	private static final Logger logger = LoggerFactory.getLogger(RabbitReceiveTopic.class);

	private static final String EXCHANGE_NAME = "topic_logs";

	public static void main(String[] argv) throws Exception {
		Connection connection = MyConnectionFactory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		String queueName = channel.queueDeclare().getQueue();
		String routingKey = TopicRoutingKey.randomOne(true);
		channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

		logger.debug("Waiting for messages:" + routingKey +", To exit press CTRL+C");

		while(true){
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				logger.debug("Received '" + message + "'");
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException ignored) {
				}
			};
			channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
		}
	}
}
