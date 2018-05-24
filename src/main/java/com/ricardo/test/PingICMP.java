package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ricardo.ping.util.OperationalSystem;
import com.ricardo.ping.util.SystemHelper;

public class PingICMP extends PingAbstract{

	public String icmpPing(String host) throws IOException {
		
		return executePing(host);
	}

	public static void main(String[] args) throws IOException {
		// asmin.com oranum.com
		/*
		 * try{ InetAddress address = InetAddress.getByName("http://www.jasmin.com"); boolean reachable =
		 * address.isReachable(10000); System.out.println("Is host reachable? " + reachable); } catch (Exception e){
		 * e.printStackTrace(); }
		 */
		String osName = System.getProperty("os.name").toLowerCase();

		// System.out.println(PingICMP.isReachable("jasmin.com"));
	}


	

}
