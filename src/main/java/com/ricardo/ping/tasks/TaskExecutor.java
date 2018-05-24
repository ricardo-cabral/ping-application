package com.ricardo.ping.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskExecutor {

	// TODO find a way to calculated the cores automatically
	//http://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	private PingTask task;

	public TaskExecutor(PingTask task) {
		this.task = task;
	}

	public void beginExecution(int hour, int minute, int second) {
		Runnable taskRunner = new Runnable() {

			@Override
			public void run() {
				task.execute();
				beginExecution(hour, minute, second);

			}
		};

		long delay = calculateDelay(hour, minute, second);
		executorService.schedule(taskRunner, delay, TimeUnit.SECONDS);
	}

	private long calculateDelay(int targetHour, int targetMin, int targetSec) {
		LocalDateTime now = LocalDateTime.now();
		ZoneId currentZone = ZoneId.systemDefault();
		ZonedDateTime zonedNow = ZonedDateTime.of(now, currentZone);
		ZonedDateTime zonedNextTarget = zonedNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
		if (zonedNow.compareTo(zonedNextTarget) > 0)
			zonedNextTarget = zonedNextTarget.plusDays(1);

		Duration duration = Duration.between(zonedNow, zonedNextTarget);
		return duration.getSeconds();
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
