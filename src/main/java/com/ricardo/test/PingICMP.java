package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ricardo.ping.tasks.PingTask;
import com.ricardo.ping.tasks.TaskExecutor;
import com.ricardo.ping.util.OperationalSystem;
import com.ricardo.ping.util.SystemHelper;

public class PingICMP extends PingAbstract implements PingTask{

	private String host;
	
	public PingICMP(String host) {
		this.host = host;
	}

	public static void main(String[] args) throws IOException {
		PingICMP ping = new PingICMP("jasmin.com");
		
		TaskExecutor executor = new TaskExecutor(ping);
		executor.beginExecution(5);
		
	}

	@Override
	public String execute() {
		String result = null;
		try {
			result = executePing(host);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}


	

}
