package future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureTest {
	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		Future[] futures = new Future[4];
		for (int index = 0; index < 12; index ++) {
			executorService.execute(new MyRunnable(index));
			executorService.submit(new MyRunnable(index));
		}
		executorService.shutdown();
	}
	static class MyRunnable implements Runnable {
		int index = 0;
		public MyRunnable(int index) {
			this.index = index;
		}
		@Override
		public void run() {
			System.out.println(index);
		}
	}
}