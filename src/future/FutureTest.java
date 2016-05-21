package future;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FutureTest {
	public static void main(String[] args) {
		final LinkedList<FutureTask<Integer>> linkedList = new LinkedList<FutureTask<Integer>>();
		for (int index = 0; index < 12; index ++) {
			FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable(index));
			linkedList.add(futureTask);
		}
		final ExecutorService executorService = Executors.newFixedThreadPool(4);
		Iterator<FutureTask<Integer>> iterator = linkedList.iterator();
		while (iterator.hasNext()) {
			executorService.submit(iterator.next());
		}
		executorService.shutdown();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					boolean isFinished = true;
					for (int index = 0; index < linkedList.size(); index ++) {
						isFinished &= linkedList.get(index).isDone();
					}
					if (isFinished) {
						System.out.println("运行完成");
						break;
					}
				}
			}
		}).start();
	}
	static class MyCallable implements Callable<Integer> {
		final int index;
		public MyCallable(int index) {
			this.index = index;
		}
		@Override
		public Integer call() throws Exception {
			Thread.sleep(index*300);
			System.out.println(index);
			return index * index;
		}
	}
	static void test01() {

		final ExecutorService executorService = Executors.newFixedThreadPool(4);
		for (int index = 0; index < 12; index ++) {
//			executorService.execute(new MyRunnable(index));
			executorService.submit(new MyRunnable(index));
//			System.out.println();
		}
		executorService.shutdown();

		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (executorService.isShutdown()) {
						System.out.println("Finished !");
						break;
					}
				}
			}
		}).start();
	
	}
}

class MyRunnable implements Runnable {
	final int index;
	public MyRunnable(int index) {
		this.index = index;
	}
	@Override
	public void run() {
		try {
			Thread.sleep(index * 1000 / 2);
			System.out.println(index);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}