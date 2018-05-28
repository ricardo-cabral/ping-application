package com.ricardo.ping.model;

import java.time.LocalDateTime;
import java.util.List;

public class PingResponse {

	private String url;
	private String ip;
	private String dataBytes;
	private LocalDateTime pingDateAndTime;
	private List<String> linesResult;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public LocalDateTime getPingDateAndTime() {
		return pingDateAndTime;
	}

	public void setPingDateAndTime(LocalDateTime pingDateAndTime) {
		this.pingDateAndTime = pingDateAndTime;
	}

	public List<String> getLinesResult() {
		return linesResult;
	}

	public void setLinesResult(List<String> linesResult) {
		this.linesResult = linesResult;
	}

	public String toString() {
		return "Website: " + url + " - I.P: " + ip + " - Databytes: " + dataBytes + " Data Time: " + pingDateAndTime;
	}
}
