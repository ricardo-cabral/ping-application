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
	/**
	 * {
	 * 	"host": "globo.com",
	 * 	"icmp_ping": "result lines of the last icmp ping command1",
	 * 	"tcp_ping": "result lines of the last tcp ping command",
	 * 	"trace": "result lines of the last trace command"
	 * }
	 * @throws IOException 
	 */

	public static Response postPingResult(Report report) throws IOException {
		Client client =  ClientBuilder.newBuilder().property("connection.timeout", 100).build();
		Response response = client.target(TARGET_URL).request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(report));
		
		System.out.println(response.toString());
		
		return response;
	}
}
