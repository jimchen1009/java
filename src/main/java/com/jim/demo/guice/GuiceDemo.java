package com.jim.demo.guice;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class GuiceDemo {

	@Qualifier
	@Retention(RUNTIME)
	@interface Message {}

	@Qualifier
	@Retention(RUNTIME)
	@interface Count {}


	/**
	 * Guice module that provides bindings for message and count used in
	 * {@link Greeter}.
	 */
	static class DemoModule extends AbstractModule {
		@Provides
		@Count
		static Integer provideCount() {
			return 3;
		}

		@Provides
		@Message
		static String provideMessage() {
			return "hello world";
		}

		@Override
		protected void configure() {
		}
	}


	static class Greeter {
		private final String message;
		private final int count;

		// Greeter declares that it needs a string message and an integer
		// representing the number of time the message to be printed.
		// The @Inject annotation marks this constructor as eligible to be used by
		// Guice.
		@Inject
		Greeter(@Message String message, @Count int count) {
			this.message = message;
			this.count = count;
		}

		void sayHello() {
			for (int i=0; i < count; i++) {
				System.out.println(message);
			}
		}
	}


	public static void main(String[] args) {
		/*
		 * Guice.createInjector() takes one or more modules, and returns a new Injector
		 * instance. Most applications will call this method exactly once, in their
		 * main() method.
		 */
		Injector injector = Guice.createInjector(new DemoModule());

		Key<String> stringKey = Key.get(String.class, Message.class);
		/*
		 * Now that we've got the injector, we can build objects.
		 */
		Greeter greeter0 = injector.getInstance(Greeter.class);
		Greeter greeter1 = injector.getInstance(Greeter.class);

		// Prints "hello world" 3 times to the console.
		greeter0.sayHello();
		greeter1.sayHello();
	}
}