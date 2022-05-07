package com.jim.demo.swaphot.agent;

import com.jim.common.PoolThreadFactory;
import com.jim.demo.swaphot.JVMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvmstat.monitor.MonitorException;

import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InstrumentationMain {

	private static final Logger logger = LoggerFactory.getLogger(InstrumentationMain.class);

	public static void main(String[] args) throws MonitorException, URISyntaxException {
		int processId = JVMUtils.getProcessId(InstrumentationMain.class);
		logger.debug("processId: {}", processId);
		InstrumentationClass instance = new InstrumentationClass("main");
		PoolThreadFactory factory = new PoolThreadFactory("hotfix", false);
		factory.newThread( ()->{
			for (int i = 0; i < 5000; i++) {
				try {
					TimeUnit.SECONDS.sleep(5);
					InstrumentationClass aClass = new InstrumentationClass("class" + i);
					String string = UUID.randomUUID().toString().replaceAll("-", "");
					logger.debug("new version:{} {} -> {}", aClass.getVersion(), string, aClass.calculate(string));
					logger.debug("instance version:{} {} -> {}", instance.getVersion(), string, instance.calculate(string));
				}
				catch (Throwable t){
					logger.error("", t);
				}
			}
		}).start();
	}
}
