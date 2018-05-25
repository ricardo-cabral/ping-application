package com.ricardo.ping.report;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public enum ReportMemoryDao {
	INSTANCE;
		
	private ConcurrentMap<String, Report> reportMap = new ConcurrentHashMap<>();

	public Report create(String host, Report report) {
		return reportMap.putIfAbsent(host, report);
	}

	public Report get(String host) {
		return reportMap.get(host);
	}

	public Report update(Report report) {
		if (report != null && reportMap.get(report.getHost()) != null)
			return reportMap.replace(report.getHost(), report);

		return null;
	}

	public List<Report> listReports() {
		return reportMap.values().stream().sorted(Comparator.comparing((Report r) -> r.getHost())).collect(Collectors.toList());
	}

}
