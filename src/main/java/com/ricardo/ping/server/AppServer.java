package com.ricardo.ping.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.ricardo.ping.service.ReportService;
import com.ricardo.ping.tasks.ProcessTask;
import com.ricardo.ping.tasks.TaskExecutor;
import com.ricardo.ping.util.SystemHelper;
import com.ricardo.test.PingICMP;
import com.ricardo.test.PingTCPIP;
import com.ricardo.test.Traceroute;

public class AppServer {

	private static Logger LOG = Logger.getLogger(AppServer.class.getName());
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
		/*PingICMP ping = new PingICMP("uol.com.br");
		PingICMP ping2 = new PingICMP("www.globo.com.br");
		PingICMP ping3 = new PingICMP("http://www.google.com");
		PingICMP ping4 = new PingICMP("asdfg.lu");
		PingICMP ping5 = new PingICMP("jasmin.com");
		PingICMP ping6 = new PingICMP("oranum.com");
		
		PingTCPIP ping7 = new PingTCPIP("uol.com.br", 300);
		PingTCPIP ping8 = new PingTCPIP("http://www.globo.com.br", 100);
		PingTCPIP ping9 = new PingTCPIP("http://www.google.com", 500);
		PingTCPIP ping10 = new PingTCPIP("asdfg.lu", 100);
		PingTCPIP ping11 = new PingTCPIP("http://www.jasmin.com", 100);
		PingTCPIP ping12 = new PingTCPIP("http://www.oranum.com", 300);
		 
		Traceroute tracert = new Traceroute("http://www.google.com");
		Traceroute tracert2 = new Traceroute("www.globo.com");
		//localhost:8090/report/
		//localhost:8090/report/errors
		
		tasks.add(ping);
		tasks.add(ping2);
		tasks.add(ping3);
		tasks.add(ping4);
		tasks.add(ping5);
		tasks.add(ping6);
		
		tasks.add(ping7);
		tasks.add(ping8);
		tasks.add(ping9);
		tasks.add(ping10);
		tasks.add(ping11);
		tasks.add(ping12);
		
		tasks.add(tracert);
		tasks.add(tracert2);*/
		LOG.info("Scheduling ping tasks for " + Arrays.toString(tasks.toArray()));
		
		TaskExecutor executor = new TaskExecutor(tasks);
		executor.beginExecution(period, delay);
		LOG.info("Scheduling ping tasks done");

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
