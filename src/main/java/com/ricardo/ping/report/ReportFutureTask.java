package com.ricardo.ping.report;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import com.ricardo.ping.web.ReportGenerator;

public class ReportFutureTask  {

	 private ExecutorService executor = Executors.newFixedThreadPool(1);
	
	
	public Future<Response> callReportController(Report report) throws Exception {
		
		return executor.submit(() -> {
			return ReportGenerator.postPingResult(report);
		});
	}

}
