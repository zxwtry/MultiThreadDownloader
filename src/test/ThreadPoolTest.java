package test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/*
 * 	采用定长线程池
 * 		即实例化newFixedThreadPool
 * 	先进行测试代码编写
 */

public class ThreadPoolTest {
	static int numOfThreads = 3;
	static Map<String, Integer> map = new HashMap<String, Integer>();
	static int valueCount = 0;
	public static void main(String[] args) {
//		System.out.println(4 << 20);
		myNewFixedThreadPool();
		
	}
	static void myNewFixedThreadPool() {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(numOfThreads);
		for (int index = 0; index < 20; index ++) {
			final int indexInThread = index;
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(700);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("ThreadName:"+Thread.currentThread().getName()+
							"\t...\t"+"ThreadID:"+Thread.currentThread().getId()+
							"\t...\tindex:"+indexInThread);  
					System.out.println(getValueFromString(Thread.currentThread().getName()));
				}
			});
		}
		fixedThreadPool.shutdown();
	}
	static class MyThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable r) {
			Thread newThread = new Thread(r);
			return newThread;
		}
	}
	static int getValueFromString(String threadName) {
		if (map.containsKey(threadName))
			return map.get(threadName);
		else {
			map.put(threadName, valueCount);
			return valueCount ++;
		}
	}
}