package com.ximuyi.demo.swaphot.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.ximuyi.demo.swaphot.JVMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvmstat.monitor.MonitorException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class InstrumentationLoader {

	private static final Logger logger = LoggerFactory.getLogger(InstrumentationLoader.class);

	//下面是直接build好的jar包，直接把jar内的文件替换成MANIFSET.MF就可以~
	private static final String JAR_PATH = "D:\\demo\\java\\build\\libs\\java-1.0-SNAPSHOT.jar";

	public static void main(String[] args) throws IOException, AttachNotSupportedException {
		JVMUtils.consumeVirtualMachine(InstrumentationMain.class, virtualMachine -> {
			try {
				File file = new File(JAR_PATH);
				if (!file.exists()){
					logger.error("it doesn't exist, {}", JAR_PATH);
					return;
				}
				virtualMachine.loadAgent(JAR_PATH, "cxs");
				logger.info("Ok");
			} catch (AgentLoadException | AgentInitializationException | IOException e) {
				logger.error("", e);
			}
		});
	}
}
