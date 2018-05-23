package com.ricardo.ping.util;

public enum OperationalSystem {
	MAC, LINUX, WINDOWS, UNKNOWN;

	public OperationalSystem getOS() {
		String osName = System.getProperty("os.name").toLowerCase();

		if (osName.toLowerCase().contains("windows")) {
			return WINDOWS;
		} else if (osName.toLowerCase().contains("mac")) {
			return MAC;
		} else if (osName.toLowerCase().contains("linux")) {
			return LINUX;
		} else {
			return UNKNOWN;
		}
	}
}
