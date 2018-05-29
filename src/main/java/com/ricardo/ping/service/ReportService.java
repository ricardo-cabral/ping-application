package com.ricardo.ping.service;

import java.util.logging.Level;
import java.util.logging.Logger;

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

import com.ricardo.ping.ProcessAbstract;
import com.ricardo.ping.report.Report;
import com.ricardo.ping.report.ReportMemoryDao;
import com.ricardo.ping.tasks.TaskExecutor;
import com.ricardo.ping.util.SystemHelper;

@Path("/report")
public class ReportService {

	private static Logger LOG = Logger.getLogger(ReportService.class.getName());
	private ReportMemoryDao dao = ReportMemoryDao.INSTANCE;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{host}")
	public String getReport(@PathParam("host") String host) {
		LOG.log(Level.FINE, host);
		Jsonb jsonb = JsonbBuilder.create();
		return jsonb.toJson(dao.get(host));
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    public Response saveReport(String json) {
		LOG.log(Level.FINE, json);
		Jsonb jsonb = JsonbBuilder.create();
		Report report = jsonb.fromJson(json, Report.class);
		
		//Update only the changed value
		Report persisted = dao.get(report.getHost());
		Report result = null;
		if(persisted != null) {
			result = this.parseReport(report, persisted);
			dao.update(result );
		}else {
			result = dao.create(report.getHost(), report );
		}
		return Response.status(HttpStatus.OK_200).entity(result).build();
	}
	
	private Report parseReport(Report report, Report persisted) {
		Report parsed = new Report();
		parsed.setHost(report.getHost());
		parsed.setIcmpPing(SystemHelper.isBlank(report.getIcmpPing()) ? persisted.getIcmpPing() :report.getIcmpPing() );
		parsed.setTcpPing(SystemHelper.isBlank(report.getTcpPing()) ?  persisted.getTcpPing() : report.getTcpPing()  );
		parsed.setTrace(SystemHelper.isBlank(report.getTrace()) ? persisted.getTrace() : report.getTrace());
		return parsed;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String listLastHostResults() {
		Jsonb jsonb = JsonbBuilder.create();
		return jsonb.toJson(ProcessAbstract.getLastPingResultsByHost());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all")
	public String listErrors() {
		Jsonb jsonb = JsonbBuilder.create();
		return jsonb.toJson(dao.listReports());
	}
}
