package com.ximuyi.demo.swaphot.agent;

public class InstrumentationClass {

	public static String MAIN_VERSION = "RC1";


	private final String name;
	private final String version;

	public InstrumentationClass(String name) {
		this.name = name;
		this.version = "0.0";
	}

	public String getVersion(){
		return MAIN_VERSION + "." + version;
	}

	public String calculate(String string){
		return string.replaceAll("[a-z]", "*");
	}
}
