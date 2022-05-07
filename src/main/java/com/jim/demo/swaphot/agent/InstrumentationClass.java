package com.jim.demo.swaphot.agent;

public class InstrumentationClass {

	//public static String MAIN_VERSION = "HOT";
	public static String MAIN_VERSION = "RC";


	private final String name;
	private final String version;

	public InstrumentationClass(String name) {
		this.name = name;
		//this.version = "5.5";
		this.version = "0.0";
	}

	public String getVersion(){
		return MAIN_VERSION + "." + version;
	}

	public String calculate(String string){
		//char[] chars = string.toCharArray();
		//Arrays.sort(chars);
		//return String.valueOf(chars);
		return string.replaceAll("[a-z]", "*");
	}
}
