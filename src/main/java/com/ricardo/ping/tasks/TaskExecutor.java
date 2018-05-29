package com.ricardo.ping.tasks;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ricardo.ping.server.AppServer;

public class TaskExecutor {

	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(AppServer.getThreadPool());
	private Set<ProcessTask> tasks;
	private String lastResult = null;

	public TaskExecutor(Set<ProcessTask> tasks) {
		this.tasks = tasks;
	}

	private Runnable executeTask(ProcessTask task) {
		return () -> {lastResult = task.execute();};
	}

	public void beginExecution(long period, long delay) {
		for (ProcessTask pingTask : tasks) {
			executorService.scheduleAtFixedRate(executeTask(pingTask), period, delay, TimeUnit.SECONDS);
		}
	}

	public void stop() {
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException ex) {
			Logger.getLogger(TaskExecutor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
