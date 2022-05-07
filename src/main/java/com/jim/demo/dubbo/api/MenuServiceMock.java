package com.jim.demo.dubbo.api;

import java.util.Collections;
import java.util.List;

/**
 * 正常形式的跟MenuServiceStub不是一样的原理:
 * MenuServiceStub是代理客户端的接口
 * MenuServiceMock是异常捕捉后代替处理接口
 *
 * 参考：
 * MockClusterInvoker
 *
 * 在MenuServiceStub 触发的异常是没有作用的，因为mock的执行是在调用真正的接口才会做处理
 *
 * 在同步异步的情况下都是可以处理的~
 */
public class MenuServiceMock implements IMenuService{

	public MenuServiceMock() {
	}

	@Override
	public String sayHi(String name) {
		// You can return mock data, this method is only executed when an RpcException is thrown.
		return String.format("Hi, %s, mock invoke sayHi because of exception", name);
	}

	@Override
	public List<String> foodList() {
		// You can return mock data, this method is only executed when an RpcException is thrown.
		return Collections.singletonList("mock-exception");
	}
}
