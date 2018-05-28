package com.ricardo.ping.model;

public class PingResponse {

	private String website;
	private String ip;
	private String dataBytes;
	
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDataBytes() {
		return dataBytes;
	}
	public void setDataBytes(String dataBytes) {
		this.dataBytes = dataBytes;
	}
	
	public String toString() {
		return "Website: " + website + "I.P: " + ip + "Databytes: " + dataBytes;
	}
}
