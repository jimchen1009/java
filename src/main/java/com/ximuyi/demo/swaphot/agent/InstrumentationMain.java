package com.ximuyi.demo.swaphot.agent;

import com.ximuyi.common.PoolThreadFactory;
import com.ximuyi.demo.swaphot.JVMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvmstat.monitor.MonitorException;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class InstrumentationMain {

	private static final Logger logger = LoggerFactory.getLogger(InstrumentationMain.class);

	public static void main(String[] args) throws MonitorException, URISyntaxException {
		int processId = JVMUtils.getProcessId(InstrumentationMain.class);
		logger.debug("processId: {}", processId);
		InstrumentationClass instrumentationClass = new InstrumentationClass("main");
		PoolThreadFactory factory = new PoolThreadFactory("hotfix", false);
		factory.newThread( ()->{
			for (int i = 0; i < 5000; i++) {
				try {
					TimeUnit.SECONDS.sleep(10);
					InstrumentationClass aClass = new InstrumentationClass("class" + i);
				logger.debug("version:{} version:{}", aClass.getVersion(), instrumentationClass.getVersion());
				}
				catch (Throwable t){
					logger.error("", t);
				}
			}
		}).start();
	}
}
