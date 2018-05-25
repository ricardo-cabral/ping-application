package com.ricardo.ping.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.ricardo.ping.service.ReportService;

public class AppServer {

	public static void main(String[] args) throws Exception {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		
		Server jettyServer = new Server(8080);
		jettyServer.setHandler(context);
		
		ServletHolder servlet = context.addServlet(ServletContainer.class, "/*");
		servlet.setInitOrder(0);
		
		 // Tells the Jersey Servlet which REST service/class to load.
        servlet.setInitParameter(
           "jersey.config.server.provider.classnames",
           ReportService.class.getCanonicalName());
        
        try {
        	jettyServer.start();
        	jettyServer.join();
        }finally {
			jettyServer.destroy();
		}
	}
}
