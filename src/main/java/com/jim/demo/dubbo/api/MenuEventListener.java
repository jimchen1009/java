package com.jim.demo.dubbo.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MenuEventListener implements IMenuEventListener {

	private static final Logger logger = LoggerFactory.getLogger(MenuEventListener.class);

	@Override
	public void onReturnSayHi(String message) {
		logger.info("onReturn {}", message);
	}

	@Override
	public void onReturnFoodList(List<String> foodList) {
		logger.info("onReturn {}", foodList);
	}

	@Override
	public void onInvokeSayHi(String message) {
		logger.info("onInvoke {}", message);
	}

	@Override
	public void onInvokeFoodList() {
		logger.info("onInvoke");
	}
}
