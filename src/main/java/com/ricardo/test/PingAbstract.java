package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ricardo.ping.model.PingResponse;
import com.ricardo.ping.server.AppServer;
import com.ricardo.ping.util.OperationalSystem;
import com.ricardo.ping.util.SystemHelper;

public abstract class PingAbstract {
	
	private static Logger LOG = Logger.getLogger(PingAbstract.class.getName());
	private List<String> buildCommand(String ipAddress) throws IOException
    {
		
		Properties properties = SystemHelper.loadProperties();
		
		List<String> command = new ArrayList<>();
		
		String pingCommand = properties.getProperty("ping.command");
		
		if( pingCommand != null && !pingCommand.equals("")) {
			
			String[] commands = pingCommand.split(" ");
			command.addAll(Arrays.asList(commands));
		}else {
			command.add("ping");
	        
	        OperationalSystem os = SystemHelper.getOS();
	        
	        if (os.equals(OperationalSystem.WINDOWS)){        
	            command.add("-n");
	        } else if (os.equals(OperationalSystem.MAC) || os.equals(OperationalSystem.LINUX)){
	            command.add("-c");
	            
	        } else{
	            throw new UnsupportedOperationException("Unsupported operating system");
	        }

	        command.add("1");
		}
		

        command.add(ipAddress);
        //command.add("; echo \"exit_message=\"$?");

        return command;
    }
	
	/**
	 * @param ipAddress The internet protocol address to ping
	 * @return True if the address is responsive, false otherwise
	 */
	public /*List<String>*/String executePing(String ipAddress) throws IOException {
		List<String> result = new ArrayList<>();
		List<String> command = buildCommand(ipAddress);
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		StringBuilder builder = new StringBuilder();
		Process process = processBuilder.start();
		
		
		try (BufferedReader standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String outputLine;

			while ((outputLine = standardOutput.readLine()) != null) {
				builder.append(outputLine);
				result.add(outputLine);
				// Picks up Windows and Unix unreachable hosts
				//if (outputLine.toLowerCase().contains("destination host unreachable")) {
					//return false;
				//System.out.println(outputLine);
				//LOG.info(outputLine);
				}
			}
		LOG.info("Process exit value: "+process.exitValue());
		this.formatResponse(result);
		return builder.toString();
		//return result;
		}
	
	
	private void formatResponse(List<String> result) {
		String address = result.get(0);
		String[] results = address.split(" ");
		PingResponse response = new PingResponse();
		response.setIp(results[2].substring(1, results[2].length()-2));
		response.setWebsite(results[1]);
		response.setDataBytes(address.split("bytes")[0].trim());
		
		System.out.println("PingResponse: " + response ) ;
		for (String string : results) {
			System.out.println(string);
		}
		
		
		
		for(int i=0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}
	}
	
	
	
	
}
