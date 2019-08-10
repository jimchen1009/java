package com.ximuyi.demo.dubbo.annotation.impl;

import com.ximuyi.demo.dubbo.annotation.AnnotationConstants;
import com.ximuyi.demo.dubbo.annotation.api.GreetingService;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(version = AnnotationConstants.VERSION)
public class AnnotationGreetingServiceImpl implements GreetingService {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationGreetingServiceImpl.class);

	@Override
	public String greeting(String name) {
		logger.info("provider received invoke of greeting: {}", name);
		//sleepWhile();
		return "Annotation, greeting " + name;
	}

	public String replyGreeting(String name) {
		logger.info("provider received invoke of replyGreeting: {}", name);
		//sleepWhile();
		return "Annotation, replyGreeting " + name;
	}

	private void sleepWhile() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
	}
}
