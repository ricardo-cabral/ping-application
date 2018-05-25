package com.ricardo.ping.tasks;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskExecutor {

	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	private Set<PingTask> tasks;
	private String lastResult = null;

	public TaskExecutor(Set<PingTask> tasks) {
		this.tasks = tasks;
	}
	
/*	 private Callable<String> executeTask() {
	    return () -> {
	        return task.execute();
	    };
	}*/
	
	 private Runnable executeTask(PingTask task) {
		    return () -> {
		    	/*try {
					TimeUnit.SECONDS.sleep(2);*/
					lastResult = task.execute();
			        System.out.println("lastResult: " + lastResult);
				/*} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
		        
		    };
		}

	public String beginExecution(long period, long delay) {
		for (PingTask pingTask : tasks) {
			executorService.scheduleAtFixedRate(executeTask(pingTask), period, delay, TimeUnit.SECONDS);	
		}
		
		String result = null;
		
		/*try {
		    result = executed.get();
		} catch (InterruptedException | ExecutionException e) {
		    e.printStackTrace();
		}*/
		
		return result;
	}
	
	public void stop() {
		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException ex) {
			Logger.getLogger(TaskExecutor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static void main(String[] args) {
		
	}
}
