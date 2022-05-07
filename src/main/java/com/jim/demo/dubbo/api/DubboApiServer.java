package com.jim.demo.dubbo.api;

import com.jim.demo.dubbo.api.monitor.MonitorServiceImpl;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.monitor.MonitorService;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DubboApiServer {

	public static void main(String[] args) throws InterruptedException {
		/**
		 * 所有的配置参数，都会拼接在zookeeper的pathname上面，例如：methodConfigs
		 * /dubbo/com.jim.demo.dubbo.api.IMenuService/providers/dubbo%3A%2F%2F192.168.56.1%3A20880%2Fcom.jim.demo.dubbo.api.IMenuService%3Fanyhost%3Dtrue%26application%3Ddubbo-provider%26application.version%3D1.0.0%26deprecated%3Dfalse%26dubbo%3D2.0.2%26dynamic%3Dtrue%26generic%3Dfalse%26interface%3Dcom.jim.demo.dubbo.api.IMenuService%26methods%3DsayHi%26pid%3D250480%26register%3Dtrue%26release%3D2.7.3%26sayHi.retries%3D1%26sayHi.timeout%3D1000%26side%3Dprovider%26timestamp%3D1564144798682
		 * /dubbo/com.jim.demo.dubbo.api.IMenuService/providers/dubbo%3A%2F%2F192.168.56.1%3A20880%2Fcom.jim.demo.dubbo.api.IMenuService%3Fanyhost%3Dtrue%26application%3Ddubbo-provider%26application.version%3D1.0.0%26deprecated%3Dfalse%26dubbo%3D2.0.2%26dynamic%3Dtrue%26generic%3Dfalse%26interface%3Dcom.jim.demo.dubbo.api.IMenuService%26methods%3DsayHi%26pid%3D221596%26register%3Dtrue%26release%3D2.7.3%26side%3Dprovider%26timestamp%3D1564144245010
		 **/
		;
		ServiceConfig<MonitorService> monitorConfig = getServiceConfig(MonitorService.class);
		monitorConfig.setRef(new MonitorServiceImpl());
		monitorConfig.export();

		ServiceConfig<IMenuService> serviceConfig = getServiceConfig(IMenuService.class);
		serviceConfig.setRef(new MenuServiceImpl(serviceConfig.getGroup()));
		serviceConfig.export();

		System.out.println("dubbo service started");
		new CountDownLatch(1).await();
	}

	private static <T> ServiceConfig<T> getServiceConfig(Class<T> interfaceClass){
		ServiceConfig<T> serviceConfig = new ServiceConfig<>();
		serviceConfig.setApplication(DubboConfigs.applicationConfig("dubbo-provider", 20221));
		serviceConfig.setRegistries(DubboConfigs.registryConfigs());
		serviceConfig.setInterface(interfaceClass);
		//service.setRef(new MenuServiceAsync(groupName));
		serviceConfig.setGroup(DubboConfigs.serviceGroupName());
		/**
		 * Control the concurrency of all method for a specified service interface at server-side
		 * Limit each method of com.foo.BarService to no more than 10 concurrent server-side executions (or take up thread pool threads):
		 */
		serviceConfig.setExecutes(1000);
		List<MethodConfig> methodConfigs = DubboConfigs.methodConfigs(interfaceClass);
		for (MethodConfig methodConfig : methodConfigs) {
			/**
			 *  Control the concurrency of specified method for a specified service interface at server-side
			 * Limit the sayHello method of com.foo.BarService to no more than 10 concurrent server-side
			 */
			methodConfig.setExecutes(500);
		}
		serviceConfig.setMethods(DubboConfigs.methodConfigs(interfaceClass));
		serviceConfig.setProtocols(DubboConfigs.serverProtocolConfigs());
		serviceConfig.setVersion(DubboConfigs.serviceVersion());
		//serviceConfig.setDelay(5000);
		serviceConfig.setMetadataReportConfig(DubboConfigs.metadataReportConfig());
		return serviceConfig;
	}
}
