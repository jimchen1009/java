package com.ximuyi.demo.dubbo.api.callback;

import com.ximuyi.common.PoolThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * The parameter callback is the same as calling a local callback or listener, just declare which parameter is a callback type in Spring's configuration file,
 * and Dubbo will generate a reverse proxy based on the long connection so that client logic can be called from the server.
 * 注意说明：说明client的本地方法会在服务端被调用
 *
 * 服务端收到的信息，自己需要自己new一个跟客户端一样的数据。
 *
 */
public class CallbackServiceImpl implements ICallbackService {

	private static final Logger logger = LoggerFactory.getLogger(CallbackServiceImpl.class);

	private final Map<String, ICallbackListener> listeners = new ConcurrentHashMap<>();
	private final PoolThreadFactory threadFactory = new PoolThreadFactory("CallbackServiceImpl", true);

	public CallbackServiceImpl() {
		threadFactory.newThread(() -> {
			while(true) {
				try {
					for (Map.Entry<String, ICallbackListener> entry : listeners.entrySet()) {
						try {
							String message = mesaageChanged(entry.getKey(), "change");
							entry.getValue().changed(message);
						} catch (Throwable t) {
							listeners.remove(entry.getKey());
						}
					}
					TimeUnit.SECONDS.sleep(5);
				}
				catch (Throwable t) { // Defense fault tolerance
					logger.error("", t);
				}
			}
		}).start();
	}

	@Override
	public void addListener(String key, ICallbackListener listener) {
		listeners.put(key, listener);
		listener.changed(mesaageChanged(key, "add")); // send change notification
	}

	@Override
	public void removeListener(String key) {
		ICallbackListener listener = listeners.remove(key);
		if (listener != null){
			listener.changed(mesaageChanged(key, "remove")); // send change notification
		}
	}

	public String mesaageChanged(String key, String message) {
		return String.format("%s: key[%s] %s", message, key, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}
}
