package com.ximuyi.demo.dubbo.annotation;

import com.ximuyi.common.PoolThreadFactory;
import com.ximuyi.demo.dubbo.annotation.action.AnnotationAction;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class AnnotationConsumerBootstrap {

	private static final Logger logger = LoggerFactory.getLogger(AnnotationConsumerBootstrap.class);

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
		context.start();
		final AnnotationAction annotationAction = (AnnotationAction) context.getBean("annotationAction");

		PoolThreadFactory factory = new PoolThreadFactory("dubbo-client", false);
		String[] methods = new String[]{"doSayHello", "doSayGoodbye", "doGreeting", "replyGreeting"};
		factory.newThread(()->{
			for (int i = 0; i < 100; i++) {
				try {
					TimeUnit.SECONDS.sleep(5);
					String methodName = methods[i % methods.length];
					Method method = AnnotationAction.class.getMethod(methodName, String.class);
					String params = "world-" + i;
					Object invoke = method.invoke(annotationAction, params);
					logger.info("annotationAction.{}('{}'): {}", methodName, params, invoke);
				} catch (Throwable e) {
				}
			}
		}).start();
	}


	@Configuration
	@EnableDubbo(scanBasePackages = "com.ximuyi.demo.dubbo.annotation.action")
	@PropertySource("classpath:/dubbo/annotation/dubbo-consumer.properties")
	@ComponentScan(value = {"com.ximuyi.demo.dubbo.annotation.action"})
	static public class ConsumerConfiguration {

	}
}
