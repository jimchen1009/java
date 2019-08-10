package com.ximuyi.demo.dubbo.api;

import com.ximuyi.common.PoolThreadFactory;
import org.apache.dubbo.rpc.AsyncContext;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class MenuServiceAsync extends MenuService {

	private static final Logger logger = LoggerFactory.getLogger(MenuServiceAsync.class);

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2, new PoolThreadFactory("MenuServiceAsync"));

	public MenuServiceAsync(String name) {
		super(name);
	}

	@Override
	public String sayHi(String name) {
		asyncInvoke(()-> String.format("hi %s, I'm MenuServiceAsync(%s). %s(ms)", name, this.name, System.currentTimeMillis()));
		return null;
	}

	@Override
	public List<String> foodList() {
		asyncInvoke(()-> new ArrayList<>(foods));
		return null;
	}

	private void asyncInvoke(Supplier<Object> supplier){
		AsyncContext context = RpcContext.startAsync();
		scheduler.submit(()->{
			context.signalContextSwitch();
			logger.info("RpcContext.startAsync() is running~");
			logInvokeContext();
			try {
				TimeUnit.SECONDS.sleep(30);
			} catch (InterruptedException ignored) {
				logger.error("", ignored);
			}
			Object message = supplier.get();
			logger.info("RpcContext.startAsync() is ended.");
			context.write(message);
		});
	}
}
