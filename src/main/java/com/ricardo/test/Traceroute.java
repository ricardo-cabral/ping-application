package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import com.ricardo.ping.model.PingResponse;
import com.ricardo.ping.report.Report;
import com.ricardo.ping.report.ReportFutureTask;
import com.ricardo.ping.tasks.ProcessTask;
import com.ricardo.ping.util.OperationalSystem;
import com.ricardo.ping.util.SystemHelper;

public class Traceroute extends ProcessAbstract implements ProcessTask{

	private static Logger LOG = Logger.getLogger(Traceroute.class.getName());
	
	private final String url;
	
	public Traceroute(String url) {
		this.url = url;
	}
	@Override
	public String execute() {
		String result = null;
		try {
			result = executeTracert(url);
		} catch (IOException | InterruptedException | URISyntaxException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String executeTracert(String url) throws IOException, InterruptedException, URISyntaxException {
		List<String> result = new ArrayList<>();
		List<String> command = buildCommand(url);
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		StringBuffer builder = new StringBuffer();
		Process process = processBuilder.start();
		synchronized (this) {

			try (BufferedReader standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String outputLine;

				while ((outputLine = standardOutput.readLine()) != null) {
					builder.append(outputLine);

					if (outputLine.length() > 0) {
						result.add(outputLine);
					}
					System.out.println("Output: " + outputLine);
				}
			}

			try (BufferedReader standardOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String outputLine;

				while ((outputLine = standardOutput.readLine()) != null) {
					builder.append(outputLine);

					if (outputLine.length() > 0) {
						result.add(outputLine);
					}
					
					System.out.println("Output: " + outputLine);
					
				}
			}

			process.waitFor();
			LOG.info("Process exit value: " + process.exitValue());
			if (process.exitValue() != 0) {
				LOG.warning("Error tracert!!!");
				this.callReport(url, result);
			} else {
				System.out.println("IP: " + url);
				this.callReport(url, result);
				this.formatResponse(url, result);
			}
		}

		return builder.toString();

	}
	
	protected void callReport(String url, List<String> result) {

		Report report = new Report();
		report.setHost(url);
		
		StringBuilder b = new StringBuilder();
		result.forEach(b::append);
		report.setTrace(result.toString());

		ReportFutureTask task = new ReportFutureTask();
		try {
			Future<Response> future = task.callReportController(report);
			Response resultPostCall = future.get();

			System.out.println("response future result: " + resultPostCall);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void formatResponse(String url, List<String> result) {
		try {
			PingResponse response = new PingResponse();
			response.setUrl(url);
			response.setPingDateAndTime(LocalDateTime.now());
			
			List<String> pingResponseLines = new ArrayList<>();
			for (int i = 1; i < result.size(); i++) {
				pingResponseLines.add(result.get(i));
			}
			response.setLinesResult(pingResponseLines);
			lastPingResultsByHost.compute(response.getUrl(), (key, value) -> response);

			System.out.println("Last Ping result TRACERT: " + lastPingResultsByHost.get(response.getUrl()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected List<String> buildCommand(String url) throws IOException, URISyntaxException {

		Properties properties = SystemHelper.loadProperties();

		List<String> command = new ArrayList<>();

		String pingCommand = properties.getProperty("tracert.command");

		if (pingCommand != null && !pingCommand.equals("")) {

			String[] commands = pingCommand.split(" ");
			command.addAll(Arrays.asList(commands));
		} else {
		
			OperationalSystem os = SystemHelper.getOS();

			if (os.equals(OperationalSystem.WINDOWS)) {
				command.add("tracert ");
			} else if (os.equals(OperationalSystem.MAC) || os.equals(OperationalSystem.LINUX)) {
				command.add("traceroute ");

			} else {
				throw new UnsupportedOperationException("Unsupported operating system");
			}
			
		}

		URI uri = new URI(url);
		String finalAddress =  uri.getHost() != null ?  uri.getHost() : url;
		command.add(finalAddress);

		return command;
	}

}
