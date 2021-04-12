package com.ximuyi.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceUtil {

	public static Properties getResourceAsProperties(String name) throws IOException {
		Properties properties = new Properties();
		InputStream stream = ResourceUtil.class.getClassLoader().getResourceAsStream(name);
		properties.load(stream);
		return properties;
	}

	public static InputStream getResourceAsStream(String name) throws IOException {
		return ResourceUtil.class.getClassLoader().getResourceAsStream(name);
	}
}
