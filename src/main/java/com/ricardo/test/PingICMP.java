package com.ricardo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PingICMP {

	
	public String icmpPing() {
		
		
		return null;
	}
	
	public static void main(String[] args) throws IOException{
		//asmin.com oranum.com
        /*try{
            InetAddress address = InetAddress.getByName("http://www.jasmin.com");
            boolean reachable = address.isReachable(10000);

            System.out.println("Is host reachable? " + reachable);
        } catch (Exception e){
            e.printStackTrace();
        }*/
		String osName = System.getProperty("os.name").toLowerCase();
		
		System.out.println(PingICMP.isReachable("jasmin.com"));
    }
	
	  /**
     * @param ipAddress The internet protocol address to ping
     * @return True if the address is responsive, false otherwise
     */
    public static boolean isReachable(String ipAddress) throws IOException
    {
        List<String> command = buildCommand(ipAddress);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        try (BufferedReader standardOutput = new BufferedReader(new InputStreamReader(process.getInputStream())))
        {
            String outputLine;

            while ((outputLine = standardOutput.readLine()) != null)
            {
            	System.out.println(outputLine);
                // Picks up Windows and Unix unreachable hosts
                if (outputLine.toLowerCase().contains("destination host unreachable"))
                {
                    return false;
                }
            }
        }

        return true;
    }

    private static List<String> buildCommand(String ipAddress)
    {
        List<String> command = new ArrayList<>();
        command.add("ping");
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.toLowerCase().contains("windows"))
        {
            command.add("-n");
        } else if (osName.toLowerCase().contains("mac") || osName.toLowerCase().contains("linux"))
        {
            command.add("-c");
        } else
        {
            throw new UnsupportedOperationException("Unsupported operating system");
        }

        command.add("1");
        command.add(ipAddress);

        return command;
    }
	
}
