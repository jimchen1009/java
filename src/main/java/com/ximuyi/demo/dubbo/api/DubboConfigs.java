package com.ximuyi.demo.dubbo.api;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DubboConfigs {

	public static String serviceGroupName(){
		List<String> groupNames = serviceGroupNames();
		return groupNames.size() == 1 ? groupNames.get(0) : "*";

	}

	public static List<String> serviceGroupNames(){
		/**
		 * hen you have multi-impls of a interface,you can distinguish them with the group.
		 */
		return Arrays.asList("group");
	}

	public static String serviceVersion(){
		/***
		 * When an interface to achieve an incompatible upgrade, you can use the version number transition.
		 * Different versions of the services do not reference each other.
		 *
		 * You can follow the steps below for version migration:
		 *
		 * In the low pressure period, upgrade to half of the provider to the new version
		 * Then upgrade all consumers to the new version
		 * Then upgrade the remaining half providers to the new version
		 */
		return "1.0.0";
	}

	public static ApplicationConfig applicationConfig(String name, int qosPort){
		ApplicationConfig applicationConfig = new ApplicationConfig(name);
		applicationConfig.setQosPort(qosPort);
		applicationConfig.setVersion("2.0.0");
		/***
		 * org.apache.dubbo.common.logger.slf4j.Slf4jLoggerAdapter
		 * org.apache.dubbo.common.logger.jcl.JclLoggerAdapter
		 * org.apache.dubbo.common.logger.log4j.Log4jLoggerAdapter
		 * org.apache.dubbo.common.logger.log4j2.Log4j2LoggerAdapter
		 * org.apache.dubbo.common.logger.jdk.JdkLoggerAdapter
		 */
		applicationConfig.setLogger("slf4j");
		return applicationConfig;
	}

	public static List<RegistryConfig> registryConfigs() {
		return Arrays.asList(
				registryConfig("main", "zookeeper://127.0.0.1:2181", true),
				registryConfig("second", "zookeeper://127.0.0.1:2181", false)
		);
	}

	public static RegistryConfig registryConfig(String id, String address, boolean isDefault){
		/**
		 * dynamic mode is disabled when service provider initially registers, then we need to enable it manually.
		 * When disconnects, the setting will not be deleted automatically, need to disable it manually.
		 */
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setId(id);
		registryConfig.setAddress(address);
		registryConfig.setDynamic(true);
		registryConfig.setCheck(true);
		registryConfig.setDefault(isDefault);
		return registryConfig;
	}

	public static List<ProtocolConfig> protocolConfigs(){
		return Arrays.asList(
				//protocolConfig("rmi", 1099),
				//protocolConfig("hessian", 8080),
				protocolConfig("dubbo", 20880)
				);
	}

	public static ProtocolConfig protocolConfig(String name, int port){
		/***
		 * Dispatcher:
		 * all: All messages will be dispatched to thread pool, including request, response, connect event, disconnect event and heartbeat.
		 * direct: All messages will not be dispatched to thread pool and will be executed directly by I/O thread.
		 * message: Only request, response messages will be dispatched to I/O thread. Other messages like disconnect, connect, heartbeat messages will be executed by I/O thread.
		 * execution: Only request message will be dispatched to thread pool. Other messages like response, connect, disconnect, heartbeat will be directly executed by I/O thread.
		 * connection: I/O thread will put disconnect and connect events in the queue and execute them sequentially, other messages will be dispatched to the thread pool.
		 *
		 * Thread pool:
		 * fixed: A fixed size of thread pool. It creates threads when starts, never shut down.（default).
		 * cached: A cached thread pool. Automatically delete the thread when it’s in idle for one minute. Recreate when needed.
		 * limit: elastic thread pool. But it can only increase the size of the thread pool. The reason is to avoid performance issue caused by traffic spike when decrease the size of the thread pool.
		 */
		ProtocolConfig protocolConfig = new ProtocolConfig(name);
		protocolConfig.setPort(port);
		protocolConfig.setDispatcher("all");
		protocolConfig.setThreadpool("fixed");
		protocolConfig.setThreads(10);
		protocolConfig.setCorethreads(10);
		protocolConfig.setIothreads(10);
		return protocolConfig;
	}

	public static List<MethodConfig> methodConfigs(Class<?> cls){
		Method[] methods = cls.getMethods();
		List<MethodConfig> configList = new ArrayList<>();
		for (Method method : methods) {
			MethodConfig config = methodConfig(method.getName(), 60000, 1);
			configList.add(config);
		}
		return configList;
	}

	public static MethodConfig methodConfig(String name, int timeout, int retries){
		MethodConfig methodConfig = new MethodConfig();
		methodConfig.setName(name);
		methodConfig.setTimeout(timeout);
		methodConfig.setRetries(retries);
		return methodConfig;
	}
}
