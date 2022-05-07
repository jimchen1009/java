package com.jim.demo.dubbo.annotation;

import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.CountDownLatch;

public class AnnotationProviderBootstrap {
	public static void main(String[] args) throws Exception {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProviderConfiguration.class);
		context.start();

		System.out.println("dubbo service started.");
		new CountDownLatch(1).await();
	}

	@Configuration
	@EnableDubbo(scanBasePackages = "com.jim.demo.dubbo.annotation.impl")
	@PropertySource("classpath:/dubbo/annotation/dubbo-provider.properties")
	static public class ProviderConfiguration {
		@Bean
		public ProviderConfig providerConfig() {
			ProviderConfig providerConfig = new ProviderConfig();
			providerConfig.setTimeout(1000);
			return providerConfig;
		}
	}
}
