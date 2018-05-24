package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.ricardo.ping.util.OperationalSystem;
import com.ricardo.ping.util.SystemHelper;

public abstract class PingAbstract {
	
	
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

        return command;
    }
	
	/**
	 * @param ipAddress The internet protocol address to ping
	 * @return True if the address is responsive, false otherwise
	 */
	public String executePing(String ipAddress) throws IOException {
		List<String> command = buildCommand(ipAddress);
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		StringBuilder builder = new StringBuilder();
		Process process = processBuilder.start();

		try (BufferedReader standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String outputLine;

			while ((outputLine = standardOutput.readLine()) != null) {
				builder.append(outputLine);
				// Picks up Windows and Unix unreachable hosts
				//if (outputLine.toLowerCase().contains("destination host unreachable")) {
					//return false;
				System.out.println(outputLine);
				}
			}
		
		return builder.toString();
		}

	
	
}
