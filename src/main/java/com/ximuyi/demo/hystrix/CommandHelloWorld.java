package com.ximuyi.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandHelloWorld extends HystrixCommand<String> {

	private static final Logger logger = LoggerFactory.getLogger(CommandHelloWorld.class);

	private final String name;

	public CommandHelloWorld(String name) {
		super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
		this.name = name;
	}

	@Override
	protected String run() {
		// a real example would do work like a network call here
		return "Hello " + name + "!";
	}


	public static class UnitTest {

		@Test
		public void testSynchronous() {
			String execute = new CommandHelloWorld("World").execute();
			logger.debug(execute);
		}
	}
}
