package com.ricardo.ping.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.ricardo.ping.PingICMP;
import com.ricardo.ping.PingTCPIP;
import com.ricardo.ping.Traceroute;
import com.ricardo.ping.service.ReportService;
import com.ricardo.ping.tasks.ProcessTask;
import com.ricardo.ping.tasks.TaskExecutor;
import com.ricardo.ping.util.SystemHelper;

public class AppServer {

	private static final Logger logger = Logger.getLogger(AppServer.class.getName());
	private static int threadPool;
	private static List<String> hosts;
	

    
	public static void main(String[] args) throws Exception {
	
		
		if(args.length > 0) {
			hosts = new ArrayList<>(args.length);
			for (String host : args) {
				hosts.add(host);
			}
		}
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		
		Server jettyServer = new Server(8090);
		jettyServer.setHandler(context);
		
		ServletHolder servlet = context.addServlet(ServletContainer.class, "/*");
		servlet.setInitOrder(0);
		
		 // Tells the Jersey Servlet which REST service/class to load.
        servlet.setInitParameter(
           "jersey.config.server.provider.classnames",
           ReportService.class.getCanonicalName());
        
        try {
        	setupTasks();
        	jettyServer.start();
        	jettyServer.join();
        	
        }finally {
			jettyServer.destroy();
		}
	}
	
	private static void setupTasks() throws IOException {
		Properties properties = SystemHelper.loadProperties();
		Integer period = Integer.valueOf(properties.getProperty("period"));
		Integer delay = Integer.valueOf(properties.getProperty("delay"));
		threadPool = Integer.valueOf(properties.getProperty("thread.pool"));
		
		Set<ProcessTask> tasks = new HashSet<>();
		if(hosts != null && hosts.size() > 0 ) {
			for (String host : hosts) {
				setupTasks(tasks, host);
			}
			
		}else {
			String standardHosts = properties.getProperty("hosts");
			String[] hosts = standardHosts.split(",");
			
			for (String host : hosts) {
				setupTasks(tasks, host);
			}
		}
	
		logger.info("Scheduling ping tasks for " + Arrays.toString(tasks.toArray()));
		
		TaskExecutor executor = new TaskExecutor(tasks);
		executor.beginExecution(period, delay);
		logger.info("Scheduling ping tasks done");

	}

	private static void setupTasks(Set<ProcessTask> tasks, String host) {
		PingICMP pingIcmp = new PingICMP(host);
		PingTCPIP pingTcp = new PingTCPIP(host, 500);
		Traceroute trace = new Traceroute(host);
		
		tasks.add(pingIcmp);
		tasks.add(pingTcp);
		tasks.add(trace);
	}
	
	public static int getThreadPool() {
		return threadPool;
	}
}
