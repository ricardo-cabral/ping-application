package com.ricardo.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.ricardo.ping.util.SystemHelper;

public class SystemHelperTest {

	
	@Test
	public void shouldPingIcmp() throws IOException {
		Properties properties = SystemHelper.loadProperties();
		assertThat(properties).containsKeys("ping.command", "delay");
	}
	
}
