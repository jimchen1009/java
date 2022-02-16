package com.ximuyi.common;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.io.InputStream;

public class Log4jUtil {

	public static final void initializeV2() throws IOException {
		InputStream stream = ResourceUtil.getResourceAsStream("log4j2.xml");
		final ConfigurationSource source = new ConfigurationSource(stream);
		Configurator.initialize(null, source);
	}
}
