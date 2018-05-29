package com.ricardo.ping;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import com.ricardo.ping.model.PingResponse;
import com.ricardo.ping.report.Report;
import com.ricardo.ping.report.ReportFutureTask;
import com.ricardo.ping.service.ReportService;
import com.ricardo.ping.util.OperationalSystem;
import com.ricardo.ping.util.SystemHelper;


public abstract class ProcessAbstract {

	private static Logger logger = Logger.getLogger(ProcessAbstract.class.getName());
	
	protected static final ConcurrentHashMap<String, PingResponse> lastPingResultsByHost = new ConcurrentHashMap<>();
	        

	protected List<String> buildCommand(String url) throws IOException, URISyntaxException {

		Properties properties = SystemHelper.loadProperties();

		List<String> command = new ArrayList<>();
		String pingCommand = properties.getProperty("ping.command");
		if (pingCommand != null && !pingCommand.equals("")) {

			String[] commands = pingCommand.split(" ");
			command.addAll(Arrays.asList(commands));
		} else {
			command.add("ping");

			OperationalSystem os = SystemHelper.getOS();

			if (os.equals(OperationalSystem.WINDOWS)) {
				command.add("-n");
			} else if (os.equals(OperationalSystem.MAC) || os.equals(OperationalSystem.LINUX)) {
				command.add("-c");

			} else {
				throw new UnsupportedOperationException("Unsupported operating system");
			}

			command.add("1");
		}

		URI uri = new URI(url);
		command.add(uri.getHost());

		return command;
	}
	
	//abstract void callReport(String url, List<String> result) ;

	abstract Report getReport(String url, List<String> result);
	
	protected void callReport(String url, List<String> result) {

		Report report = getReport(url, result);

		ReportFutureTask task = new ReportFutureTask();
		try {
			Future<Response> future = task.callReportController(report);
			Response resultPostCall = future.get();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "error calling report", e);
			e.printStackTrace();
		}

	}
	public static ConcurrentHashMap<String, PingResponse> getLastPingResultsByHost() {
		return lastPingResultsByHost;
	}

}
