package com.ximuyi.demo.swaphot.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public class InstrumentationAgent {

	private static final Logger logger = LoggerFactory.getLogger(InstrumentationAgent.class);

	private static final String CLS_NAME = "com.ximuyi.demo.swaphot.agent.InstrumentationClass";
	//从build出来的文件中复制过来
	private static final String CLS_PATH = "D:\\demo\\java\\src\\main\\java\\com\\ximuyi\\demo\\swaphot\\agent\\InstrumentationClass.class";

	public static void agentmain(String arg, Instrumentation instrumentation) throws Exception {
		// only if header utility is on the class path; otherwise,
		// a class can be found within any class loader by iterating
		// over the return value of Instrumentation::getAllLoadedClasses
		Class<?> aClass = Class.forName(CLS_NAME);

		// copy the contents of typo.fix into a byte array
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (InputStream input = new FileInputStream(CLS_PATH)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = input.read(buffer)) != -1) {
				output.write(buffer, 0, length);
			}
		}

		// Apply the redefinition
		instrumentation.redefineClasses(new ClassDefinition(aClass, output.toByteArray()));
		logger.info("{} Apply the redefinition: {}", arg, aClass.getName());
	}
}
