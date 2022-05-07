package com.jim.demo.dubbo.annotation.impl;

import com.jim.demo.dubbo.annotation.AnnotationConstants;
import com.jim.demo.dubbo.annotation.api.HelloService;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(version = AnnotationConstants.VERSION, methods = {@Method(name = "sayHello", timeout = 250, retries = 1, async = true)})
public class AnnotationHelloServiceImpl implements HelloService {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationHelloServiceImpl.class);

	public String sayHello(String name) {
		logger.info("provider received invoke of sayHello: {}", name);
		return "Annotation, sayHello " + name;
	}

	public String sayGoodbye(String name) {
		logger.info("provider received invoke of sayGoodbye: {}", name);
		return "Annotation, Goodbye " + name;
	}
}

