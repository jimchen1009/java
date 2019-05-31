package com.ximuyi.demo.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RabbitReceiveLogs {
	private static final Logger logger = LoggerFactory.getLogger(RabbitReceiveLogs.class);

	private static final String EXCHANGE_NAME = "logs";

	public static void main(String[] argv) throws Exception {
		Connection connection = MyConectionFactory.newConnection();
		Channel channel = connection.createChannel();

		/***
		 * We will use a direct exchange instead. The routing algorithm behind a direct exchange is simple
		 * a message goes to the queues whose binding key exactly matches the routing key of the message.
		 */
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		//String queueName = channel.queueDeclare("A", false, false, false, null).getQueue();
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, EXCHANGE_NAME, "");

		logger.debug("Waiting for messages. To exit press CTRL+C");

		while(true){
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				logger.debug("Received '" + message + "'");
				try {
					TimeUnit.SECONDS.sleep(0);
				} catch (InterruptedException ignored) {
				}
			};
			channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
		}
	}
}
