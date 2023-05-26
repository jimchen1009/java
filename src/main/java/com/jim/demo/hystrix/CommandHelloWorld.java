package com.jim.demo.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

public class CommandHelloWorld extends HystrixObservableCommand<String> {

	private static final Logger logger = LoggerFactory.getLogger(CommandHelloWorld.class);

	private final String name;

	public CommandHelloWorld(String name) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("HelloWorld")));
		this.name = name;
	}

	@Override
	protected Observable<String> resumeWithFallback() {
		return Observable.just("failed!");
	}

	@Override
	protected String getCacheKey() {
		return super.getCacheKey();
	}

	@Override
	protected Observable<String> construct() {
		return Observable.create((Observable.OnSubscribe<String>) observer -> {
			try {
				if (!observer.isUnsubscribed()) {
					// a real example would do work like a network call here
					observer.onNext("Hello " + name + "!");
					if (System.currentTimeMillis() > 0){
						throw new HystrixBadRequestException("");
					}
					observer.onCompleted();
				}
			} catch (Exception e) {
				observer.onError(e);
			}
		}).subscribeOn(Schedulers.io());
	}
}
