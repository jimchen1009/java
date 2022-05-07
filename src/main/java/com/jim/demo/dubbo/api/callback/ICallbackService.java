package com.jim.demo.dubbo.api.callback;

public interface ICallbackService {

	void addListener(String key, ICallbackListener listener);

	void removeListener(String key);
}
