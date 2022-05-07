package com.jim.demo.dubbo.annotation.action;

import com.jim.demo.dubbo.annotation.AnnotationConstants;
import com.jim.demo.dubbo.annotation.api.GreetingService;
import com.jim.demo.dubbo.annotation.api.HelloService;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("annotationAction")
public class AnnotationAction {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationAction.class);

	@Reference(interfaceClass = HelloService.class, version = AnnotationConstants.VERSION)
	private HelloService helloService;


	@Reference(interfaceClass = GreetingService.class,
			version = AnnotationConstants.VERSION,
			methods = {@Method(name = "greeting", timeout = 250, retries = 1)})
	private GreetingService greetingService;



	public String doSayHello(String name) {
		try {
			return helloService.sayHello(name);
		} catch (Exception e) {
			logger.info("", e);
			return "Throw Exception";
		}
	}

	public String doSayGoodbye(String name) {
		try {
			return helloService.sayGoodbye(name);
		} catch (Exception e) {
			e.printStackTrace();
			return "Throw Exception";
		}

	}

	public String doGreeting(String name) {
		try {
			return greetingService.greeting(name);
		} catch (Exception e) {
			logger.info("", e);
			return "Throw Exception";
		}

	}

	public String replyGreeting(String name) {
		try {
			return greetingService.replyGreeting(name);
		} catch (Exception e) {
			logger.info("", e);
			return "Throw Exception";
		}
	}
}
