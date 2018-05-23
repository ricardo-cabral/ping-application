package com.ricardo.test;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.Before;
import org.junit.Test;



public class PingICMPTest {

	private PingICMP icmpPing;
	
	@Before
	public void setUp() {
		icmpPing = new PingICMP();
	}
	
	@Test
	public void shouldPingIcmp() {
		assertThat(icmpPing.icmpPing()).isNotEmpty();
	}
	
	
	
	
}
