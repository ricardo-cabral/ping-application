package com.ricardo.ping.model;

public class Ping {

	private final String pingCommand = "ping";
	private String command;
	private String host;
	
	public Ping(String command, String host) {
		this.command = command;
		this.host = host;
	}
	
	public String getPingCommand() {
		return pingCommand + " " +command + " " + host; 
	}
}
