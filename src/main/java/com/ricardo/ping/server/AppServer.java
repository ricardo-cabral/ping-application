package com.ricardo.ping.server;

import java.io.IOException;
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
import com.ricardo.ping.tasks.PingTask;
import com.ricardo.ping.tasks.TaskExecutor;
import com.ricardo.ping.util.SystemHelper;
import com.ricardo.test.PingICMP;

public class AppServer {

	private static Logger LOG = Logger.getLogger(AppServer.class.getName());
	
	public static void main(String[] args) throws Exception {
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
		PingICMP ping = new PingICMP("uol.com.br");
		//PingICMP ping2 = new PingICMP("globo.com.br");
		//PingICMP ping3 = new PingICMP("www.google.com");
		PingICMP ping4 = new PingICMP("asdfg.lu");
		
		Set<PingTask> tasks = new HashSet<>();
		tasks.add(ping);
		//tasks.add(ping2);
		//tasks.add(ping3);
		tasks.add(ping4);
		LOG.info("Scheduling ping tasks for " + tasks.toString());
		
		TaskExecutor executor = new TaskExecutor(tasks);
		executor.beginExecution(15, 2);
		LOG.info("Scheduling ping tasks done");
		
		/*TaskExecutor executor2 = new TaskExecutor(ping2);
		executor.beginExecution(1, 2);
		*/
	}
}
