package com.ricardo.ping;

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
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.ricardo.ping.model.PingResponse;
import com.ricardo.ping.report.Report;
import com.ricardo.ping.tasks.ProcessTask;
import com.ricardo.ping.util.OperationalSystem;
import com.ricardo.ping.util.SystemHelper;

public class PingICMP extends ProcessAbstract implements ProcessTask {

	private static Logger logger = Logger.getLogger(PingICMP.class.getName());
	private String url;

	public PingICMP(String url) {
		this.url = url;
	}

	@Override
	public String execute() {
		String result = null;
		try {
			result = executePing(url);
		} catch (IOException | InterruptedException | URISyntaxException e) {
			logger.log(Level.SEVERE,"error  executing PingICM", e);
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param url
	 * @return a String containing the result of Ping ICMP operation
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws URISyntaxException
	 */
	public String executePing(String url) throws IOException, InterruptedException, URISyntaxException {
		List<String> result = new ArrayList<>();
		List<String> command = buildCommand(url);
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		StringBuffer buffer = new StringBuffer();
		Process process = processBuilder.start();
		synchronized (this) {

			try (BufferedReader standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String outputLine;

				while ((outputLine = standardOutput.readLine()) != null) {
					buffer.append(outputLine);

					if (outputLine.length() > 0) {
						result.add(outputLine);
					}
					logger.log(Level.FINE, outputLine);
				}
			}

			try (BufferedReader standardOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
				String outputLine;

				while ((outputLine = standardOutput.readLine()) != null) {
					buffer.append(outputLine);

					if (outputLine.length() > 0) {
						result.add(outputLine);
					}

					logger.log(Level.FINE, outputLine);
				}
			}

			process.waitFor();
			logger.log(Level.FINE, "Process exit value: " + process.exitValue());
			if (process.exitValue() != 0) {
				logger.log(Level.SEVERE, " Error pinging website:  " + url);
				this.callReport(url, result);
			} else {
				this.callReport(url, result);
				this.formatResponse(url, result);
			}
		}

		return buffer.toString();

	}

	/**
	 * 
	 * @param url
	 * @return  A list of String containing the commands in case it is not specified in application.properties
	 * @throws IOException
	 * @throws URISyntaxException
	 */
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
		String finalAddress =  uri.getHost() != null ?  uri.getHost() : url;
		command.add(finalAddress);

		return command;
	}

	private void formatResponse(String url, List<String> result) {
			PingResponse response = new PingResponse();
			
			response.setUrl(url);
			response.setPingDateAndTime(LocalDateTime.now());

			List<String> pingResponseLines = new ArrayList<>();
			for (int i = 1; i < result.size(); i++) {
				pingResponseLines.add(result.get(i));
			}
			response.setLinesResult(pingResponseLines);
			lastPingResultsByHost.compute(response.getUrl(), (key, value) -> response);
	}

	@Override
	Report getReport(String url, List<String> result) {
		Report report = new Report();
		report.setHost(url);
		
		StringBuilder b = new StringBuilder();
		result.forEach(b::append);
		report.setIcmpPing(b.toString());
		
		return report;
	}
	
	
}
