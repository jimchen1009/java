package com.ximuyi.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	public static Properties getResourceAsStream(String name) throws IOException {
		Properties properties = new Properties();
		InputStream stream = PropertiesUtil.class.getClassLoader().getResourceAsStream(name);
		properties.load(stream);
		return properties;
	}
}
