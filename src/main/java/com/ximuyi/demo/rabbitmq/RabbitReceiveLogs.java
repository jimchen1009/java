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
		 *
		 * direct：exchange在和queue进行binding时会设置routingkey，将消息发送到exchange时会设置对应的routingkey，只有这两个routingkey完全相同，exchange才会选择对应的binging进行消息路由。
		 *
		 * fanout：直接将消息路由到所有绑定的队列中，无须对消息的routingkey进行匹配操作。（广播）
		 *
		 * topic：此类型exchange和direct类型差不多，但direct类型要求routingkey完全相等，这里的routingkey可以有通配符：'*','#'。
		 *
		 **/
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
