package com.ximuyi.demo.dubbo.api;

import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuService implements IMenuService {

	private static final Logger logger = LoggerFactory.getLogger(MenuService.class);

	protected final String name;
	protected final List<String> foods;

	public MenuService(String name) {
		this.name = name;
		this.foods = create(name);
	}

	public String getName() {
		return name;
	}

	protected void logInvokeContext(){
		//logger.info("attachments: {}", RpcContext.getContext().getAttachments());
		//DubboUtil.logRpcContext(logger, "getContext", RpcContext.getContext());
	}

	private List<String> create(String name){
		List<String> foods = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			foods.add(String.format("%s.%s", name, "food-" + i));
		}
		return foods;
	}
}
