package prj.riverly.thread;

import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestThreadPool {

	private RequestThreadFactory factory = new RequestThreadFactory();
	
	private int threadCount = 10;
	private int currentThreadCount = 0;
	private long timeToIdleMillisecond = 3000;
	
	private final LinkedBlockingQueue<FutureTask<?>> taskQue = new LinkedBlockingQueue<FutureTask<?>>();
	
	private RequestThreadPool() {};
	
	private static class ThreadPoolHolder {
		private static final RequestThreadPool instance = new RequestThreadPool();
	}
	
	public static RequestThreadPool getInstance() {
		return ThreadPoolHolder.instance;
	}
	
	public <V> FutureTask<V> task(Callable<V> callable) {
		FutureTask<V> futureTask = new FutureTask<V>(callable);
		
		while(!taskQue.offer(futureTask)) Thread.yield();
		
		if(currentThreadCount > 0) Thread.yield();
		
		synchronized (this) {
			if(currentThreadCount < threadCount && taskQue.size() > 0) {
				factory.newThread(() -> {
					long idleTime = getMilliSecond(); 
					while(true) {
						try {
							FutureTask<?> task = taskQue.poll(timeToIdleMillisecond, TimeUnit.MILLISECONDS);
							
							if(task != null) {
								idleTime = getMilliSecond();
								task.run();
							}
							
							if(idleTime + timeToIdleMillisecond < getMilliSecond()) {
								currentThreadCount--;
								break;
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
				
				currentThreadCount++;
			}
		};
		
		return futureTask;
	}
	
	public void setThreadCount(int threadCount) {
		if(threadCount < 1) throw new IllegalArgumentException();
		this.threadCount = threadCount;
	}
	
	public void setTimeToIdle(long millisecond) {
		if(millisecond < 1) throw new IllegalArgumentException();
		this.timeToIdleMillisecond = millisecond;
	}
	
	public void shutdown() {
		taskQue.clear();
		factory.shutdown();
	}
	
	private long getMilliSecond() {
		return Instant.now().toEpochMilli();
	}
	
	private class RequestThreadFactory implements ThreadFactory {

		private final AtomicInteger threadNumber = new AtomicInteger();
		private final ThreadGroup group = new ThreadGroup("Request-Thread-Group");
		
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "Request-Thread-"+threadNumber.getAndIncrement());
			if(t.isDaemon()) t.setDaemon(false);
			return t;
		}
		
		public void shutdown() {
			group.interrupt();
		}

	}

}
