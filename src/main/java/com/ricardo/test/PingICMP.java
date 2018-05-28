package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import com.ricardo.ping.model.PingResponse;
import com.ricardo.ping.report.Report;
import com.ricardo.ping.report.ReportFutureTask;
import com.ricardo.ping.tasks.PingTask;

public class PingICMP extends PingAbstract implements PingTask {

	private static Logger LOG = Logger.getLogger(PingICMP.class.getName());
	private String host;

	public PingICMP(String host) {
		this.host = host;
	}

	@Override
	public String execute() {
		String result = null;
		try {
			result = executePing(host);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public String executePing(String ipAddress) throws IOException, InterruptedException {
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
					// Picks up Windows and Unix unreachable hosts
					// if (outputLine.toLowerCase().contains("destination host unreachable")) {
					// return false;
					System.out.println("Output: " + outputLine);
					// LOG.info(outputLine);
				}
			}
			// InputStream error = process.getErrorStream();
			// InputStream output = process.getInputStream();
			process.waitFor();
			LOG.info("Process exit value: " + process.exitValue());
			if (process.exitValue() != 0) {
				LOG.warning("Error pinging website!!!");
				this.callReport(ipAddress, result);
			} else {
				System.out.println("IP: " + ipAddress);
				this.formatResponse(result);
			}
		}

		return builder.toString();

	}

	@Override
	protected void callReport(String url, List<String> result) {

		Report report = new Report();
		report.setHost(url);
		report.setIcmpPing(result.toString());

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String substringOf(String firstLine) {
		// windows
		// int init = firstLine.indexOf("with");
		int init = firstLine.indexOf("): ");
		return firstLine.substring(init, firstLine.length() - 1);
	}



}
