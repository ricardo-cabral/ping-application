package com.ricardo.ping.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class SystemHelper {

	private static String resourceName = "application.properties"; 
	public static OperationalSystem getOS() {
		String osName = System.getProperty("os.name").toLowerCase();

		if (osName.toLowerCase().contains("windows")) {
			return OperationalSystem.WINDOWS;
		} else if (osName.toLowerCase().contains("mac")) {
			return OperationalSystem.MAC;
		} else if (osName.toLowerCase().contains("linux")) {
			return OperationalSystem.LINUX;
		} else {
			return OperationalSystem.UNKNOWN;
		}
	}

	
	public static Properties loadProperties() throws IOException {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties props = new Properties();
		try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
		    props.load(resourceStream);
		}
		
		return props;

	}
	
	public static boolean isBlank(String value) {
		return value ==null || value.isEmpty();
	}
}
