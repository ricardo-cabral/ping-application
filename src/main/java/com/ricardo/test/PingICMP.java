package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
		PingICMP ping = new PingICMP("uol.com.br");
		PingICMP ping2 = new PingICMP("globo.com.br");
		PingICMP ping3 = new PingICMP("www.google.com");
		
		Set<PingTask> tasks = new HashSet<>();
		tasks.add(ping);
		tasks.add(ping2);
		tasks.add(ping3);
		TaskExecutor executor = new TaskExecutor(tasks);
		executor.beginExecution(1, 2);
		
		
		/*TaskExecutor executor2 = new TaskExecutor(ping2);
		executor.beginExecution(1, 2);
		*/
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
