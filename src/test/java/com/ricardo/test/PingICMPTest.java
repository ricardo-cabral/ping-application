package com.ricardo.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.ricardo.ping.PingICMP;



public class PingICMPTest {

	private PingICMP icmpPing;
	
	@Before
	public void setUp() {
		//icmpPing = new PingICMP();
	}
	
	@Test
	public void shouldPingIcmp() throws IOException {
		//assertThat(icmpPing.icmpPing("uol.com.br")).isNotEmpty();
	}
	
}
