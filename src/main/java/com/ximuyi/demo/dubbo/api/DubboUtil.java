package com.ximuyi.demo.dubbo.api;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;

public class DubboUtil {

	public static void logRpcContext(Logger logger, String message, RpcContext context){
		boolean consumerSide = context.isConsumerSide();
		boolean providerSide = context.isProviderSide();
		String remoteAddress = context.getRemoteAddressString();
		String methodName = context.getMethodName();
		URL url = context.getUrl();
		logger.info("{} consumerSide:{}, providerSide:{}, remoteAddress:{}, methodName:{}, url:{}",
		            message, consumerSide, providerSide, remoteAddress, methodName, url);
	}
}
