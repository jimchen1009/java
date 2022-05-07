package com.jim.demo.swaphot.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.jim.demo.swaphot.JVMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class InstrumentationLoader {

	private static final Logger logger = LoggerFactory.getLogger(InstrumentationLoader.class);

	//下面是直接build好的jar包，直接把jar内的文件替换成MANIFSET.MF就可以~
	private static final String JAR_PATH = "D:\\demo\\java\\build\\libs\\java-1.0-SNAPSHOT.jar";
	//从build出来的文件中复制过来
	private static final String CLS_PATH = "D:\\demo\\java\\src\\main\\java\\com\\jim\\demo\\swaphot\\agent\\InstrumentationClass.class";

	public static void main(String[] args) throws IOException, AttachNotSupportedException {
		JVMUtils.consumeVirtualMachine(InstrumentationMain.class, virtualMachine -> {
			InstrumentationParams params = new InstrumentationParams("redefine", InstrumentationClass.class.getName(), CLS_PATH, "Jim");
			try {
				File file = new File(JAR_PATH);
				if (!file.exists()){
					logger.error("it doesn't exist, {}", JAR_PATH);
					return;
				}
				String string = params.toString();
				virtualMachine.loadAgent(JAR_PATH, string);
				logger.info("{}", string);
			} catch (AgentLoadException | AgentInitializationException | IOException e) {
				logger.error("{}", params, e);
			}
		});
	}
}
