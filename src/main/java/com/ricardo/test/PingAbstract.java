package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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

	
	
	protected static final ConcurrentHashMap<String, PingResponse> lastPingResultsByHost = new ConcurrentHashMap<>();
	        

	protected List<String> buildCommand(String ipAddress) throws IOException {

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
	
	abstract void callReport(String url, List<String> result) ;

	public static ConcurrentHashMap<String, PingResponse> getLastPingResultsByHost() {
		return lastPingResultsByHost;
	}

}
