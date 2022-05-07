package com.jim.demo.swaphot.agent;

import com.alibaba.fastjson.JSON;

public class InstrumentationParams {
	private final String cmd;
	private final String className;
	private final String classPath;
	private final String operator;

	public InstrumentationParams(String cmd, String className, String classPath, String operator) {
		this.cmd = cmd;
		this.className = className;
		this.classPath = classPath;
		this.operator = operator;
	}

	public String getCmd() {
		return cmd;
	}

	public String getClassName() {
		return className;
	}

	public String getClassPath() {
		return classPath;
	}

	public String getOperator() {
		return operator;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public static InstrumentationParams parse(String string){
		return JSON.parseObject(string, InstrumentationParams.class);
	}
}
