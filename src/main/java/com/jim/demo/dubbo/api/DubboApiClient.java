package com.jim.demo.dubbo.api;

import com.jim.common.PoolThreadFactory;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.monitor.MonitorService;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DubboApiClient {

	private static final Logger logger = LoggerFactory.getLogger(DubboApiClient.class);
	private static final MenuEventListener EventListener = new MenuEventListener();
	private static final int LOOP_COUNT = Integer.MAX_VALUE;

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		/***
		 * /dubbo/com.jim.demo.dubbo.api.IMenuService/consumers/consumer%3A%2F%2F192.168.56.1%2Fcom.jim.demo.dubbo.api.IMenuService%3Fapplication%3Ddubbo-consumer%26application.version%3D2.0.0%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.0.2%26generic%3Dfalse%26interface%3Dcom.jim.demo.dubbo.api.IMenuService%26lazy%3Dfalse%26methods%3DsayHi%26pid%3D10256%26protocol%3Ddubbo%26qos.port%3D20222%26release%3D2.7.3%26sayHi.retries%3D1%26sayHi.timeout%3D1000%26side%3Dconsumer%26sticky%3Dfalse%26timestamp%3D1564392769942
		 * /dubbo/com.jim.demo.dubbo.api.IMenuService/consumers/consumer%3A%2F%2F192.168.56.1%2Fcom.jim.demo.dubbo.api.IMenuService%3Fapplication%3Ddubbo-consumer%26application.version%3D2.0.0%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.0.2%26generic%3Dfalse%26interface%3Dcom.jim.demo.dubbo.api.IMenuService%26lazy%3Dfalse%26methods%3DsayHi%26pid%3D10256%26protocol%3Dhessian%26qos.port%3D20222%26release%3D2.7.3%26sayHi.retries%3D1%26sayHi.timeout%3D1000%26side%3Dconsumer%26sticky%3Dfalse%26timestamp%3D1564392773774
		 * /dubbo/com.jim.demo.dubbo.api.IMenuService/consumers/consumer%3A%2F%2F192.168.56.1%2Fcom.jim.demo.dubbo.api.IMenuService%3Fapplication%3Ddubbo-consumer%26application.version%3D2.0.0%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.0.2%26generic%3Dfalse%26interface%3Dcom.jim.demo.dubbo.api.IMenuService%26lazy%3Dfalse%26methods%3DsayHi%26pid%3D10256%26protocol%3Drmi%26qos.port%3D20222%26release%3D2.7.3%26sayHi.retries%3D1%26sayHi.timeout%3D1000%26side%3Dconsumer%26sticky%3Dfalse%26timestamp%3D1564392773235
		 * /dubbo/com.jim.demo.dubbo.api.IMenuService/consumers/consumer%3A%2F%2F192.168.56.1%2Fcom.jim.demo.dubbo.api.IMenuService%3Fapplication%3Ddubbo-consumer%26application.version%3D2.0.0%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.0.2%26generic%3Dfalse%26interface%3Dcom.jim.demo.dubbo.api.IMenuService%26lazy%3Dfalse%26methods%3DsayHi%26pid%3D10256%26qos.port%3D20222%26release%3D2.7.3%26sayHi.retries%3D1%26sayHi.timeout%3D1000%26side%3Dconsumer%26sticky%3Dfalse%26timestamp%3D1564392773901
		 */
		CountDownLatch downLatch = new CountDownLatch(2);
		startMenuClient(downLatch);
		startMonitorClient(downLatch);
		downLatch.await();
		ReferenceConfigCache.getCache().destroyAll();
	}

	private static void startMenuClient(CountDownLatch downLatch){
		ReferenceConfig<IMenuService> reference = configMenuReference(getReferenceConfig(IMenuService.class));
		MonitorConfig monitorConfig = DubboConfigs.monitorConfig();
		//monitor也是一个Client的配置，所以group、version等等都要跟server端匹配
		monitorConfig.setGroup(reference.getGroup());
		monitorConfig.setVersion(DubboConfigs.serviceVersion());
		reference.setMonitor(monitorConfig);
		IMenuService service = ReferenceConfigCache.getCache().get(reference);
		onLoopInvoke("menu", 5, (index)-> {
			try {
				beforeInvoke();
				//invokeHi(index, service);
				invokeHiAsync01(index, service);
				//invokeFoodList(index, service);
				//invokeFoodListAsync(index, service);
			}
			catch (Throwable t){
				logger.error("index:{}", index, t);
			}
		}, downLatch);
	}

	private static void startMonitorClient(CountDownLatch downLatch){
		ReferenceConfig<MonitorService> reference = getReferenceConfig(MonitorService.class);
		MonitorService service = ReferenceConfigCache.getCache().get(reference);
		onLoopInvoke("monitor", 10, (index)-> {
			List<URL> urlList = service.lookup(null);
			for (URL url : urlList) {
				String doneCount = url.getParameter("collect.count");
				logger.info("doneCount:{} URL:{}", doneCount, url.toIdentityString());
			}
		}, downLatch);
	}

	private static void invokeHiAsync01(int index, IMenuService service) throws ExecutionException, InterruptedException {
		String params = "dubbo" + index;
		/**
		 * 函数的调用堵塞与否取决了方法的配置，与SEVER的实现异步与否没有关系
		 * methodConfig.setAsync(methodIsAsync);
		 */
		service.sayHi(params);
		Future<String> future = RpcContext.getContext().getFuture();
		String message = future.get();
		logger.info("index:{} service.sayHi('{}'): {}", index, params, message);
	}

	private static void invokeFoodListAsync(int index, IMenuService service) throws ExecutionException, InterruptedException {
		List<String> stringList = service.foodList();
		Future<List<String>> future = RpcContext.getContext().getFuture();
		List<String> foodList = future.get();
		logger.info("index:{} the food list: {}", index, foodList);
	}


	private static void invokeHi(int index, IMenuService service){
		String params = "dubbo" + index;
		String message = service.sayHi(params);
		logger.info("index:{} service.sayHi('{}'): {}", index, params, message);
	}

	private static void invokeFoodList(int index, IMenuService service){
		logger.info("index:{} the food list: {}", index, service.foodList());
	}

	private static void beforeInvoke(){
		RpcContext context = RpcContext.getContext();
		context.setAttachment("index", "1");
	}

	private static <T> ReferenceConfig<T> getReferenceConfig(Class<T> interfaceClass){
		ReferenceConfig<T> reference = new ReferenceConfig<>();
		reference.setApplication(DubboConfigs.applicationConfig("dubbo-consumer", 20231));

		reference.setRegistries(DubboConfigs.registryConfigs());
		reference.setInterface(interfaceClass);
		//hen you have multi-impls of a interface,you can distinguish them with the group.
		reference.setGroup(DubboConfigs.serviceGroupName());
		reference.setVersion(DubboConfigs.serviceVersion());
		/**
		 * You can config the loadbalance attribute with leastactive at server-side or client-side,
		 * then the framework will make consumer call the minimum number of concurrent one.
		 */
		reference.setLoadbalance("leastactive");
		/***
		 * Control the concurrency of all method for a specified service interface at client-side
		 * Limit each method of com.foo.BarService to no more than 10 concurrent client-side executions (or take up thread pool threads)
		 */
		reference.setActives(10);
		//Limit client-side creating connection to no more than 10 connections for interface com.foo.BarService.
		reference.setConnections(10);
		reference.setLazy(true);
		reference.setMetadataReportConfig(DubboConfigs.metadataReportConfig());
		reference.setProtocol(DubboConfigs.serverProtocolConfig().getName());
		return reference;
	}

	private static String getMethodName(String prefix, MethodConfig methodConfig){
		char ch = methodConfig.getName().toUpperCase().charAt(0);
		return prefix + ch + methodConfig.getName().substring(1);
	}

	private static ReferenceConfig<IMenuService> configMenuReference(ReferenceConfig<IMenuService> reference){
		List<MethodConfig> methodConfigs = DubboConfigs.methodConfigs(IMenuService.class);
		/***
		 * lru Delete excess cache Based on the principle of least recently used. The hottest data is cached.
		 * threadlocal The current thread cache. For example, a page have a lot of portal and each portal need to check user information, you can reduce this redundant visit with this cache.
		 * jcache integrate with JSR107 , you can bridge a variety of cache implementation。
		 *
		 * org.apache.dubbo.cache.support.lru.LruCacheFactory
		 * org.apache.dubbo.cache.support.threadlocal.ThreadLocalCacheFactory
		 * org.apache.dubbo.cache.support.jcache.JCacheFactory
		 */
		for (MethodConfig methodConfig : methodConfigs) {
			if (methodConfig.getName().equals("foodList")) {
				methodConfig.setMerger("list");
			}
			/***
			 * 回调函数不成效，因为设置的group是*
			 * 调用的SEVER是：group /com.jim.demo.dubbo.api.IMenuService:1.0.0
			 * CLIENT内存保存是：* /com.jim.demo.dubbo.api.IMenuService:1.0.0
			 *
			 * 所以找不到调用的：ConsumerMethodModel.AsyncMethodInfo
			 */
			methodConfig.setOnreturn(EventListener);
			methodConfig.setOnreturnMethod(getMethodName("onReturn", methodConfig));
			methodConfig.setOninvoke(EventListener);
			methodConfig.setOninvokeMethod(getMethodName("onInvoke", methodConfig));

			//methodConfig.setCache("expiring");
			methodConfig.setAsync(true);
			/**
			 * sent="true" wait for the message to be send,if send failure，will throw exception.
			 * sent="false" do not wait for the message to be send,when the message will push into io queue,will return immediately.
			 */
			methodConfig.setSent(true);
			/**
			 * if you only want to asynchronous call,and don't care the return.you can config return="false",
			 * To reduce the cost of creating and managing Future objects.
			 */
			methodConfig.setReturn(true);
			/***
			 * Control the concurrency of specified method for a specified service interface at client-side
			 * Limit the sayHello method of com.foo.BarService to no more than 10 concurrent client-side executions(or take up thread pool threads)
			 */
			methodConfig.setActives(500);

		}
		reference.setMethods(methodConfigs);
		/***
		 * In the development and testing environment, it is often necessary to bypass the registry and test only designated service providers.
		 * In this case, point-to-point direct connection may be required, and the service provider will ignore the list of provider registration providers.
		 */
		//reference.setUrl("dubbo://localhost:20880");
		/**
		 * reference.setStub(true)
		 * Caused by: java.lang.ClassNotFoundException: com.jim.demo.dubbo.api.IMenuServiceStub
		 * 所以接口为什么需要规范成没有I开口的原因，换成使用类名的接口~
		 */
		reference.setStub(MenuServiceStub.class.getName());
		reference.setMock(MenuServiceMock.class.getName());
		return reference;
	}


	private static void onLoopInvoke(String thredName, int intervalValue, Consumer<Integer> consumer, CountDownLatch downLatch){
		PoolThreadFactory factory = new PoolThreadFactory(thredName, false);
		factory.newThread( ()->{
			for (int index = 0; index < LOOP_COUNT; index++) {
				try {
					TimeUnit.SECONDS.sleep(intervalValue);
					consumer.accept(index);
				}
				catch (Throwable t){
					logger.error("index:{}", index, t);
				}
			}
			downLatch.countDown();
		}).start();
	}

}
