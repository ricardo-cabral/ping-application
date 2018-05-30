package com.ricardo.ping;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import com.ricardo.ping.model.PingResponse;
import com.ricardo.ping.report.Report;
import com.ricardo.ping.report.ReportFutureTask;


public abstract class ProcessAbstract {

	private static Logger logger = Logger.getLogger(ProcessAbstract.class.getName());
	
	protected static final ConcurrentHashMap<String, PingResponse> lastPingResultsByHost = new ConcurrentHashMap<>();

	//Factory of Report
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
