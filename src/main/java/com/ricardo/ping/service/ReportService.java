package com.ricardo.ping.service;


import java.util.logging.Logger;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;

import com.ricardo.ping.report.Report;
import com.ricardo.ping.report.ReportMemoryDao;

@Path("/report")
public class ReportService {

	/*
	 * 
	 * localhost:8080/report/globo.com
	 * {
    "  host":"globo.com", 
    "  icmp_ping":"result lines of the last icmp ping command", 
    "  tcp_ping":"result lines of the last tcp ping command", 
    "  trace":"result lines of the last trace command"
   }
	 */
	private static Logger LOG = Logger.getLogger(ReportService.class.getName());
	ReportMemoryDao dao;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{host}")
	public String getReport(@PathParam("host") String host) {
		LOG.info(host);
		dao = ReportMemoryDao.INSTANCE;
		Jsonb jsonb = JsonbBuilder.create();
		return jsonb.toJson(dao.get(host));
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    public Response saveReport(String json) {
		//LOG.info(report.getHost());
		LOG.info(json);
		dao = ReportMemoryDao.INSTANCE;
		Jsonb jsonb = JsonbBuilder.create();
		Report report = jsonb.fromJson(json, Report.class);
		return Response.status(HttpStatus.OK_200).entity(dao.create(report.getHost(), report)).build();
	}
}
