package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.ws.rs.core.Response;

import com.ricardo.ping.model.PingResponse;
import com.ricardo.ping.report.Report;
import com.ricardo.ping.report.ReportFutureTask;
import com.ricardo.ping.server.AppServer;
import com.ricardo.ping.util.OperationalSystem;
import com.ricardo.ping.util.SystemHelper;
import com.ricardo.ping.web.ReportGenerator;

public abstract class PingAbstract {

	private static Logger LOG = Logger.getLogger(PingAbstract.class.getName());
	
	private static final ConcurrentHashMap<String, PingResponse> lastPingResultsByHost = new ConcurrentHashMap<>();
	
	
	        

	private List<String> buildCommand(String ipAddress) throws IOException {

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

		command.add(ipAddress);
		// command.add("; echo \"exit_message=\"$?");

		return command;
	}

	/**
	 * @param ipAddress The internet protocol address to ping
	 * @return True if the address is responsive, false otherwise
	 */
	public /* List<String> */String executePing(String ipAddress) throws IOException {
		List<String> result = new ArrayList<>();
		List<String> command = buildCommand(ipAddress);
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		StringBuffer builder = new StringBuffer();
		Process process = processBuilder.start();
		synchronized (process) {

			try (BufferedReader standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String outputLine;

				while ((outputLine = standardOutput.readLine()) != null) {
					builder.append(outputLine);
					
					if(outputLine.length() > 0) {
						result.add(outputLine);
					}
					// Picks up Windows and Unix unreachable hosts
					// if (outputLine.toLowerCase().contains("destination host unreachable")) {
					// return false;
					System.out.println("Output: " + outputLine);
					// LOG.info(outputLine);
				}
			}
			LOG.info("Process exit value: " + process.exitValue());
			if (process.exitValue() != 0) {
				LOG.warning("Error pinging website!!!");
				this.callReport(result);
			} else {
				System.out.println("IP: " + ipAddress);
				this.formatResponse(result);
			}
		}

		return builder.toString();
		// return result;
	}

	private void callReport(List<String> result) {
		String address = result.get(0);
		
		String url = null;
		//https://regex101.com/r/fG5pZ8/24
		String regex = "^(?:https?:\\/\\/)?(?:www\\.)?[a-zA-Z0-9./]+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(address);
		while(m.find()) {
		String urlStr = m.group();
		if (urlStr.startsWith("(") && urlStr.endsWith(")")){
		urlStr = urlStr.substring(1, urlStr.length() - 1);
		}
		}
		
		
		String[] results = address.split(" ");
		PingResponse response = new PingResponse();
		System.out.println(Arrays.toString(results));
		
		response.setIp(results[2].substring(1, results[2].length() - 2));
		response.setUrl(url);
		
		Report report = new Report();
		report.setHost(results[1]);
		
		ReportFutureTask task = new ReportFutureTask();
		try {
			Future<Response> future =  task.callReportController(report);
			Response resultPostCall = future.get();
			
			System.out.println("response future result: " + resultPostCall);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void formatResponse(List<String> result) {
		try {
		String address = result.get(0);
		String[] results = address.split(" ");
		PingResponse response = new PingResponse();
		System.out.println(Arrays.toString(results));
		
		response.setIp(results[2].substring(1, results[2].length() - 2));
		response.setUrl(results[1]);
		response.setDataBytes(substringOf(address));
		response.setPingDateAndTime(LocalDateTime.now());

		List<String> pingResponseLines = new ArrayList<>();
		for (int i = 1; i < result.size(); i++) {
			pingResponseLines.add(result.get(i));
		}
		
		lastPingResultsByHost.compute(response.getUrl(), (key, value) -> response);
		
		System.out.println("Last Ping result: " + lastPingResultsByHost.get(response.getUrl()));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String substringOf(String firstLine) {
		int init = firstLine.indexOf("with");
		return firstLine.substring(init, firstLine.length() -1);
	}

}
