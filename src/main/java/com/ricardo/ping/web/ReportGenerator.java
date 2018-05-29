package com.ricardo.ping.web;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ricardo.ping.report.Report;

public class ReportGenerator {
	
	private static final String TARGET_URL = "http://localhost:8090/report";

	public static Response postPingResult(Report report) throws IOException {
		Client client =  ClientBuilder.newBuilder().property("connection.timeout", 100).build();
		Response response = client.target(TARGET_URL).request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(report));
		
		
		return response;
	}
}
