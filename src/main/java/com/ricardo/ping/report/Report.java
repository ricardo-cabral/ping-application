package com.ricardo.ping.report;

import javax.json.bind.annotation.JsonbProperty;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//@XmlRootElement
public class Report {
	/**
	 * {"host":"the given host", "icmp_ping":"result lines of the last icmp ping command", "tcp_ping":"result lines of
	 * the last tcp ping command", "trace":"result lines of the last trace command"}
	 */

	//@XmlElement  
	private String host;
	//@JsonbProperty("icmp_ping")
	//@XmlElement  
	private String icmpPing;
	//@JsonbProperty("tcp_ping")
	//@XmlElement  
	private String tcpPing;
	//@XmlElement  
	private String trace;
	
	public Report() {
		
	}

	public Report(String host, String icmpPing, String tcpPing, String trace) {
		this.host = host;
		this.icmpPing = icmpPing;
		this.tcpPing = tcpPing;
		this.trace = trace;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setIcmpPing(String icmpPing) {
		this.icmpPing = icmpPing;
	}

	public void setTcpPing(String tcpPing) {
		this.tcpPing = tcpPing;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}

	public String getHost() {
		return host;
	}

	public String getIcmpPing() {
		return icmpPing;
	}

	public String getTcpPing() {
		return tcpPing;
	}

	public String getTrace() {
		return trace;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((icmpPing == null) ? 0 : icmpPing.hashCode());
		result = prime * result + ((tcpPing == null) ? 0 : tcpPing.hashCode());
		result = prime * result + ((trace == null) ? 0 : trace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Report other = (Report) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (icmpPing == null) {
			if (other.icmpPing != null)
				return false;
		} else if (!icmpPing.equals(other.icmpPing))
			return false;
		if (tcpPing == null) {
			if (other.tcpPing != null)
				return false;
		} else if (!tcpPing.equals(other.tcpPing))
			return false;
		if (trace == null) {
			if (other.trace != null)
				return false;
		} else if (!trace.equals(other.trace))
			return false;
		return true;
	}

}
