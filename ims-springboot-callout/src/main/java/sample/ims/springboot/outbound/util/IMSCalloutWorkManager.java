package sample.ims.springboot.outbound.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;

public class IMSCalloutWorkManager implements WorkManager {
	//static since only one Executor Thread Pool is needed
	public static ExecutorService executor = Executors.newCachedThreadPool();

	public void doWork(Work arg0) throws WorkException {
		throw new WorkException("doWork(Work arg0) not implemented");
	}

	public void doWork(Work arg0, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
		throw new WorkException("doWork(Work arg0, long arg1, ExecutionContext arg2, WorkListener arg3)) not implemented");		
	}

	public void scheduleWork(Work arg0) throws WorkException {
		executor.execute(arg0);
	}

	public void scheduleWork(Work arg0, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
		executor.execute(arg0);	
	}

	public long startWork(Work arg0) throws WorkException {
		return -1;
	}

	public long startWork(Work arg0, long arg1, ExecutionContext arg2, WorkListener arg3) throws WorkException {
		return -1;
	}

}
