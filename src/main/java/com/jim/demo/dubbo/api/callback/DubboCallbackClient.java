package com.jim.demo.dubbo.api.callback;

import com.jim.demo.dubbo.api.DubboApiClient;
import com.jim.demo.dubbo.api.DubboConfigs;
import org.apache.dubbo.config.MethodConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DubboCallbackClient {
	private static final Logger logger = LoggerFactory.getLogger(DubboApiClient.class);

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		List<ReferenceConfig<ICallbackService>> referenceList = new ArrayList<>();
		for (ProtocolConfig protocolConfig : DubboConfigs.serverProtocolConfigs()) {
			ReferenceConfig<ICallbackService>  reference = getService();
			reference.setProtocol(protocolConfig.getName());
			referenceList.add(reference);
		}
		referenceList.add(getService());
		int count = 10;
		for (int i = 0; i < count; i++) {
			TimeUnit.SECONDS.sleep(5);
			for (ReferenceConfig<ICallbackService> reference : referenceList) {
				ICallbackService service = reference.get();
				String listenerKey = String.format("client-%s", i);
				//Serialized class com.jim.demo.dubbo.api.callback.DubboCallbackClient$CallbackListener must implement java.io.Serializable
				service.addListener(listenerKey, new CallbackListener(listenerKey));
			}
		}
		for (ReferenceConfig<ICallbackService> reference : referenceList) {
			for (int i = 0; i < count; i++) {
				TimeUnit.SECONDS.sleep(5);
				ICallbackService service = reference.get();
				String listenerKey = String.format("client-%s", i);
				service.removeListener(listenerKey);
			}
		}
	}

	private static ReferenceConfig<ICallbackService> getService(){
		ReferenceConfig<ICallbackService> reference = new ReferenceConfig<>();
		reference.setApplication(DubboConfigs.applicationConfig("dubbo-consumer", 20222));
		reference.setRegistries(DubboConfigs.registryConfigs());
		reference.setInterface(ICallbackService.class);
		List<MethodConfig> methodConfigs = DubboConfigs.methodConfigs(ICallbackService.class);
		reference.setMethods(methodConfigs);
		reference.setGroup("*");
		reference.setVersion(DubboConfigs.serviceVersion());
		return reference;
	}

	private static class CallbackListener implements ICallbackListener, Serializable {

		private final String key;

		public CallbackListener() {
			this(null);
		}

		public CallbackListener(String key) {
			this.key = key;
		}

		@Override
		public void changed(String msg) {
			logger.info("{} onCallback: {}", key, msg);
		}
	}
}
