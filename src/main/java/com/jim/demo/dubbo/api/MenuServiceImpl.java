package com.jim.demo.dubbo.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MenuServiceImpl extends MenuService {

	public MenuServiceImpl(String name) {
		super(name);
	}

	@Override
	public String sayHi(String name) {
		logInvokeContext();
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
		}
		return String.format("hi %s, I'm MenuServiceImpl(%s). %s(ms)", name, this.name, System.currentTimeMillis());
	}

	@Override
	public List<String> foodList() {
		logInvokeContext();
		return new ArrayList<>(foods);
	}
}
