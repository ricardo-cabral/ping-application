package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import com.ricardo.ping.model.PingResponse;
import com.ricardo.ping.report.Report;
import com.ricardo.ping.report.ReportFutureTask;
import com.ricardo.ping.tasks.PingTask;

public class PingTCPIP extends PingAbstract implements PingTask {

	private String url;
	private int timeout;
	private long responseTimeInMillis;
	private int httpStatusCode;

	public PingTCPIP(String url, int timeout) {
		this.url = url;
		this.timeout = timeout;

	}

	@Override
	public String execute() {
		return pingURL(url, timeout);
	}

	public String pingURL(String url, int timeout) {
		url = url.replaceFirst("^https", "http");
		StringBuffer builder = new StringBuffer();
		try {

			Long startTime = Instant.now().toEpochMilli();
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("HEAD");
			
			try (BufferedReader standardOutput = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String outputLine;

				while ((outputLine = standardOutput.readLine()) != null) {
					builder.append(outputLine);

					
					System.out.println("Output: " + outputLine);
				}
			}
			
			httpStatusCode = connection.getResponseCode();
			Long endTime = Instant.now().toEpochMilli();
			responseTimeInMillis = endTime - startTime;
			
			setPingResponse();
		} catch (IOException e) {
			callReport(url, Arrays.asList(e.getMessage()));
		}
		
		return builder.toString();
	}

	@Override
	protected void callReport(String url, List<String> result) {

		Report report = new Report();
		report.setHost(url);
		report.setTcpPing(result.toString());

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
	

	private void setPingResponse() {
		try {
			
			PingResponse response = new PingResponse();
			
			response.setUrl(url);
			response.setResponseTimeInMillis(responseTimeInMillis);
			response.setHttpStatusCode(httpStatusCode);
			
			lastPingResultsByHost.compute(response.getUrl(), (key, value) -> response);

			System.out.println("Last Ping result: " + lastPingResultsByHost.get(response.getUrl()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
