package com.ximuyi.demo.swaphot.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

/**
 * 增加字段 会报错的~
 * java.lang.reflect.InvocationTargetException
 * 	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
 * 	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
 * 	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
 * 	at java.lang.reflect.Method.invoke(Method.java:483)
 * 	at sun.instrument.InstrumentationImpl.loadClassAndStartAgent(InstrumentationImpl.java:386)
 * 	at sun.instrument.InstrumentationImpl.loadClassAndCallAgentmain(InstrumentationImpl.java:411)
 * Caused by: java.lang.UnsupportedOperationException: class redefinition failed: attempted to change the schema (add/remove fields)
 * 	at sun.instrument.InstrumentationImpl.redefineClasses0(Native Method)
 * 	at sun.instrument.InstrumentationImpl.redefineClasses(InstrumentationImpl.java:170)
 * 	at com.ximuyi.demo.swaphot.agent.InstrumentationAgent.redefineClasses(InstrumentationAgent.java:47)
 * 	at com.ximuyi.demo.swaphot.agent.InstrumentationAgent.agentmain(InstrumentationAgent.java:23)
 * 	... 6 more
 */
public class InstrumentationAgent {

	private static final Logger logger = LoggerFactory.getLogger(InstrumentationAgent.class);

	public static void agentmain(String arg, Instrumentation instrumentation) throws Exception {
		InstrumentationParams params = InstrumentationParams.parse(arg);
		if (params.getCmd().equals("redefine")){
			redefineClasses(params, instrumentation);
		}
		else if (params.getCmd().equals("retransform")){
			retransformClasses(params, instrumentation);
		}
		else {
			throw new UnsupportedOperationException(params.getCmd());
		}
	}

	/**
	 * [12:49:09:689] [DEBUG] [hotfix-0] com.ximuyi.demo.swaphot.agent.InstrumentationMain.lambda$main$0(InstrumentationMain.java:26): version:OLD:V1.0.1 version:OLD:V1.0.1
	 * [12:49:19:691] [DEBUG] [hotfix-0] com.ximuyi.demo.swaphot.agent.InstrumentationMain.lambda$main$0(InstrumentationMain.java:26): version:OLD:V1.0.1 version:OLD:V1.0.1
	 * [12:49:24:301] [INFO] [Attach Listener] com.ximuyi.demo.swaphot.agent.InstrumentationAgent.agentmain(InstrumentationAgent.java:38): cxs Apply the redefinition: com.ximuyi.demo.swaphot.agent.InstrumentationClass
	 * [12:49:29:692] [DEBUG] [hotfix-0] com.ximuyi.demo.swaphot.agent.InstrumentationMain.lambda$main$0(InstrumentationMain.java:26): version:V1.5.0 version:V1.0.1
	 * [12:49:39:694] [DEBUG] [hotfix-0] com.ximuyi.demo.swaphot.agent.InstrumentationMain.lambda$main$0(InstrumentationMain.java:26): version:V1.5.0 version:V1.0.1
	 * [12:49:49:695] [DEBUG] [hotfix-0] com.ximuyi.demo.swaphot.agent.InstrumentationMain.lambda$main$0(InstrumentationMain.java:26): version:V1.5.0 version:V1.0.1
	 * InstrumentationClass.MAIN_VERSION: 这个值Reload之后并没有被修改了~
	 */
	private static void redefineClasses(InstrumentationParams params, Instrumentation instrumentation) throws ClassNotFoundException, IOException, UnmodifiableClassException {
		Class<?> aClass = Class.forName(params.getClassName());

		byte[] classBytes = loadClassBytes(params);
		// Apply the redefinition
		instrumentation.redefineClasses(new ClassDefinition(aClass, classBytes));
		logger.info("{} Apply the redefinition: {}", params.getOperator(), aClass.getName());
	}

	private static void retransformClasses(InstrumentationParams params, Instrumentation instrumentation) throws ClassNotFoundException, UnmodifiableClassException, IOException {
		Class<?> aClass = Class.forName(params.getClassName());
		byte[] classBytes = loadClassBytes(params);
		instrumentation.addTransformer(new ClassFileTransformer() {
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
				return classBytes;
			}
		}, true);

		instrumentation.retransformClasses(aClass);
	}

	private static byte[] loadClassBytes(InstrumentationParams params) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try (InputStream input = new FileInputStream(params.getClassPath())) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = input.read(buffer)) != -1) {
				output.write(buffer, 0, length);
			}
		}
		return output.toByteArray();
	}
}
