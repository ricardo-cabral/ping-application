package com.ricardo.ping.report;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.json.bind.JsonbException;
import javax.ws.rs.core.Response;

import com.ricardo.ping.web.ReportGenerator;

public class ReportFutureTask  {

	 private ExecutorService executor = Executors.newFixedThreadPool(2);
	
	
	public Future<Response> callReportController(Report report) throws Exception {
		
		return executor.submit(() -> {
			return ReportGenerator.postPingResult(report);
		});
	}

}
