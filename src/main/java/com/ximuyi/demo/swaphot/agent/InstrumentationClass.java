package com.ximuyi.demo.swaphot.agent;

public class InstrumentationClass {

	/**
	 * [12:49:09:689] [DEBUG] [hotfix-0] com.ximuyi.demo.swaphot.agent.InstrumentationMain.lambda$main$0(InstrumentationMain.java:26): version:OLD:V1.0.1 version:OLD:V1.0.1
	 * [12:49:19:691] [DEBUG] [hotfix-0] com.ximuyi.demo.swaphot.agent.InstrumentationMain.lambda$main$0(InstrumentationMain.java:26): version:OLD:V1.0.1 version:OLD:V1.0.1
	 * [12:49:24:301] [INFO] [Attach Listener] com.ximuyi.demo.swaphot.agent.InstrumentationAgent.agentmain(InstrumentationAgent.java:38): cxs Apply the redefinition: com.ximuyi.demo.swaphot.agent.InstrumentationClass
	 * [12:49:29:692] [DEBUG] [hotfix-0] com.ximuyi.demo.swaphot.agent.InstrumentationMain.lambda$main$0(InstrumentationMain.java:26): version:V1.5.0 version:V1.0.1
	 * [12:49:39:694] [DEBUG] [hotfix-0] com.ximuyi.demo.swaphot.agent.InstrumentationMain.lambda$main$0(InstrumentationMain.java:26): version:V1.5.0 version:V1.0.1
	 * [12:49:49:695] [DEBUG] [hotfix-0] com.ximuyi.demo.swaphot.agent.InstrumentationMain.lambda$main$0(InstrumentationMain.java:26): version:V1.5.0 version:V1.0.1
	 * 这个值Reload之后并没有被修改了~
	 */
	public static String MAIN_VERSION = "V1";


	private final String name;
	private final String version;

	public InstrumentationClass(String name) {
		this.name = name;
		this.version = "0.1";
	}

	public String getVersion(){
		//旧版本的没有：OLD:
		return "OLD:" + MAIN_VERSION + "." + version;
	}
}
