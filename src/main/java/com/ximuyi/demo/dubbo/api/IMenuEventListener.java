package com.ximuyi.demo.dubbo.api;

import java.util.List;

public interface IMenuEventListener {

	void onReturnSayHi(String message);

	void onReturnFoodList(List<String> foodList);

	void onInvokeSayHi(String message);

	void onInvokeFoodList();
}
