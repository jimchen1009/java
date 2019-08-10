package com.ximuyi.demo.dubbo.api;

import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DubboApiClient {

	private static final Logger logger = LoggerFactory.getLogger(DubboApiClient.class);
	private static final MenuEventListener EventListener = new MenuEventListener();

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		/***
		 * /dubbo/com.ximuyi.demo.dubbo.api.IMenuService/consumers/consumer%3A%2F%2F192.168.56.1%2Fcom.ximuyi.demo.dubbo.api.IMenuService%3Fapplication%3Ddubbo-consumer%26application.version%3D2.0.0%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.0.2%26generic%3Dfalse%26interface%3Dcom.ximuyi.demo.dubbo.api.IMenuService%26lazy%3Dfalse%26methods%3DsayHi%26pid%3D10256%26protocol%3Ddubbo%26qos.port%3D20222%26release%3D2.7.3%26sayHi.retries%3D1%26sayHi.timeout%3D1000%26side%3Dconsumer%26sticky%3Dfalse%26timestamp%3D1564392769942
		 * /dubbo/com.ximuyi.demo.dubbo.api.IMenuService/consumers/consumer%3A%2F%2F192.168.56.1%2Fcom.ximuyi.demo.dubbo.api.IMenuService%3Fapplication%3Ddubbo-consumer%26application.version%3D2.0.0%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.0.2%26generic%3Dfalse%26interface%3Dcom.ximuyi.demo.dubbo.api.IMenuService%26lazy%3Dfalse%26methods%3DsayHi%26pid%3D10256%26protocol%3Dhessian%26qos.port%3D20222%26release%3D2.7.3%26sayHi.retries%3D1%26sayHi.timeout%3D1000%26side%3Dconsumer%26sticky%3Dfalse%26timestamp%3D1564392773774
		 * /dubbo/com.ximuyi.demo.dubbo.api.IMenuService/consumers/consumer%3A%2F%2F192.168.56.1%2Fcom.ximuyi.demo.dubbo.api.IMenuService%3Fapplication%3Ddubbo-consumer%26application.version%3D2.0.0%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.0.2%26generic%3Dfalse%26interface%3Dcom.ximuyi.demo.dubbo.api.IMenuService%26lazy%3Dfalse%26methods%3DsayHi%26pid%3D10256%26protocol%3Drmi%26qos.port%3D20222%26release%3D2.7.3%26sayHi.retries%3D1%26sayHi.timeout%3D1000%26side%3Dconsumer%26sticky%3Dfalse%26timestamp%3D1564392773235
		 * /dubbo/com.ximuyi.demo.dubbo.api.IMenuService/consumers/consumer%3A%2F%2F192.168.56.1%2Fcom.ximuyi.demo.dubbo.api.IMenuService%3Fapplication%3Ddubbo-consumer%26application.version%3D2.0.0%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.0.2%26generic%3Dfalse%26interface%3Dcom.ximuyi.demo.dubbo.api.IMenuService%26lazy%3Dfalse%26methods%3DsayHi%26pid%3D10256%26qos.port%3D20222%26release%3D2.7.3%26sayHi.retries%3D1%26sayHi.timeout%3D1000%26side%3Dconsumer%26sticky%3Dfalse%26timestamp%3D1564392773901
		 */
		List<ReferenceConfig<IMenuService>> referenceList = new ArrayList<>();
		for (ProtocolConfig protocolConfig : DubboConfigs.protocolConfigs()) {
			ReferenceConfig<IMenuService>  reference = getService();
			reference.setProtocol(protocolConfig.getName());
			/**
			 * reference.setStub(true)
			 * Caused by: java.lang.ClassNotFoundException: com.ximuyi.demo.dubbo.api.IMenuServiceStub
			 * 所以接口为什么需要规范成没有I开口的原因，换成使用类名的接口~
			 */
			reference.setStub(MenuServiceStub.class.getName());
			referenceList.add(reference);
		}
		referenceList.add(getService());
		for (int i = 0; i < 1000; i++) {
			TimeUnit.SECONDS.sleep(5);
			ReferenceConfig<IMenuService> reference = referenceList.get(i % referenceList.size());
			IMenuService service = reference.get();
			beforeInvoke();
			//invokeHi(i, service);
			invokeHiAsync01(i, service);
			//invokeFoodList(i, service);
			//invokeFoodListAsync(i, service);
		}
		for (ReferenceConfig<IMenuService> reference : referenceList) {
			reference.destroy();
		}
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

	private static ReferenceConfig<IMenuService> getService(){
		ReferenceConfig<IMenuService> reference = new ReferenceConfig<>();
		reference.setApplication(DubboConfigs.applicationConfig("dubbo-consumer", 20222));

		reference.setRegistries(DubboConfigs.registryConfigs());
		reference.setInterface(IMenuService.class);
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
			 * 调用的SEVER是：group /com.ximuyi.demo.dubbo.api.IMenuService:1.0.0
			 * CLIENT内存保存是：* /com.ximuyi.demo.dubbo.api.IMenuService:1.0.0
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
		}
		reference.setMethods(methodConfigs);
		//hen you have multi-impls of a interface,you can distinguish them with the group.
		reference.setGroup(DubboConfigs.serviceGroupName());
		reference.setVersion(DubboConfigs.serviceVersion());
		/***
		 * In the development and testing environment, it is often necessary to bypass the registry and test only designated service providers.
		 * In this case, point-to-point direct connection may be required, and the service provider will ignore the list of provider registration providers.
		 */
		//reference.setUrl("dubbo://localhost:20880");
		return reference;
	}

	private static String getMethodName(String prefix, MethodConfig methodConfig){
		char ch = methodConfig.getName().toUpperCase().charAt(0);
		return prefix + ch + methodConfig.getName().substring(1);
	}
}
