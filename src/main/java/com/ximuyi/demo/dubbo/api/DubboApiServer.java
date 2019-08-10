package com.ximuyi.demo.dubbo.api;

import org.apache.dubbo.config.ServiceConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DubboApiServer {

	public static void main(String[] args) throws InterruptedException {
		/**
		 * 所有的配置参数，都会拼接在zookeeper的pathname上面，例如：methodConfigs
		 * /dubbo/com.ximuyi.demo.dubbo.api.IMenuService/providers/dubbo%3A%2F%2F192.168.56.1%3A20880%2Fcom.ximuyi.demo.dubbo.api.IMenuService%3Fanyhost%3Dtrue%26application%3Ddubbo-provider%26application.version%3D1.0.0%26deprecated%3Dfalse%26dubbo%3D2.0.2%26dynamic%3Dtrue%26generic%3Dfalse%26interface%3Dcom.ximuyi.demo.dubbo.api.IMenuService%26methods%3DsayHi%26pid%3D250480%26register%3Dtrue%26release%3D2.7.3%26sayHi.retries%3D1%26sayHi.timeout%3D1000%26side%3Dprovider%26timestamp%3D1564144798682
		 * /dubbo/com.ximuyi.demo.dubbo.api.IMenuService/providers/dubbo%3A%2F%2F192.168.56.1%3A20880%2Fcom.ximuyi.demo.dubbo.api.IMenuService%3Fanyhost%3Dtrue%26application%3Ddubbo-provider%26application.version%3D1.0.0%26deprecated%3Dfalse%26dubbo%3D2.0.2%26dynamic%3Dtrue%26generic%3Dfalse%26interface%3Dcom.ximuyi.demo.dubbo.api.IMenuService%26methods%3DsayHi%26pid%3D221596%26register%3Dtrue%26release%3D2.7.3%26side%3Dprovider%26timestamp%3D1564144245010
		 **/
		List<ServiceConfig<IMenuService>> serviceList = new ArrayList<>();
		List<String> groupNames = DubboConfigs.serviceGroupNames();
		for (String groupName : groupNames) {
			ServiceConfig<IMenuService> service = new ServiceConfig<>();
			service.setApplication(DubboConfigs.applicationConfig("dubbo-provider", 20221));
			service.setRegistries(DubboConfigs.registryConfigs());
			service.setInterface(IMenuService.class);
			service.setRef(new MenuServiceImpl(groupName));
			//service.setRef(new MenuServiceAsync(groupName));
			service.setGroup(groupName);
			service.setMethods(DubboConfigs.methodConfigs());
			service.setProtocols(DubboConfigs.protocolConfigs());
			service.setVersion(DubboConfigs.serviceVersion());
			serviceList.add(service);
		}
		serviceList.forEach(ServiceConfig::export);
		System.out.println("dubbo service started");
		new CountDownLatch(1).await();
	}
}
