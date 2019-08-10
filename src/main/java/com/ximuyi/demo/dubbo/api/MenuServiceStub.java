package com.ximuyi.demo.dubbo.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MenuServiceStub implements IMenuService{

	private static final Logger logger = LoggerFactory.getLogger(MenuServiceStub.class);

	private final IMenuService service;

	public MenuServiceStub(IMenuService service) {
		this.service = service;
	}

	@Override
	public String sayHi(String name) {
		logger.info("stub invoke sayHi for doing something locally");
		return service.sayHi(name);
	}

	@Override
	public List<String> foodList() {
		logger.info("stub invoke foodList for doing something locally");
		return service.foodList();
	}
}
