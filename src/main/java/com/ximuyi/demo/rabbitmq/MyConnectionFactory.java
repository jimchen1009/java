package com.ximuyi.demo.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MyConnectionFactory {

	private final static ConnectionFactory factory = new ConnectionFactory();
	static {
		factory.setHost("localhost");
	}

	public static Connection newConnection() throws IOException, TimeoutException {
		return factory.newConnection();
	}
}
