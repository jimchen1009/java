package com.jim.demo.dubbo.api.callback;

import com.jim.demo.dubbo.api.DubboConfigs;
import org.apache.dubbo.config.ArgumentConfig;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.ServiceConfig;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DubboCallbackService {

	public static void main(String[] args) throws InterruptedException {
		ServiceConfig<ICallbackService> serviceConfig = new ServiceConfig<>();
		serviceConfig.setApplication(DubboConfigs.applicationConfig("dubbo-provider", 20221));
		serviceConfig.setRegistries(DubboConfigs.registryConfigs());
		serviceConfig.setInterface(ICallbackService.class);
		serviceConfig.setConnections(10);
		serviceConfig.setCallbacks(1000);
		serviceConfig.setRef(new CallbackServiceImpl());
		List<MethodConfig> methodConfigs = DubboConfigs.methodConfigs(ICallbackService.class);
		for (MethodConfig methodConfig : methodConfigs) {
			if (methodConfig.getName().equals("addListener")) {
				ArgumentConfig argumentConfig = new ArgumentConfig();
				/***
				 * 写死参数：ICallbackListener 是回调接口~
				 * void addListener(String key, ICallbackListener listener);
				 */
				argumentConfig.setIndex(1);
				argumentConfig.setCallback(true);
			}
		}
		serviceConfig.setMethods(methodConfigs);
		serviceConfig.setProtocols(DubboConfigs.serverProtocolConfigs());
		serviceConfig.setVersion(DubboConfigs.serviceVersion());
		serviceConfig.export();
		System.out.println("dubbo service started");
		new CountDownLatch(1).await();
	}
}
