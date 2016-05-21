package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import util.BlockState;
import util.FileHelper;

public class MainFuture {
	static Map<String, Integer> map = new HashMap<String, Integer>();
	static int valueCount = 0;
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String urlString = scanner.next();
		String fileNameWithPosfix = FileHelper.getFileNameWithPosfixFromURL(urlString);
		final String persistFILEURI = FileHelper.getPersistFILEURI(fileNameWithPosfix);
		final BlockState blockState = FileHelper.getBlockState(urlString);
		try {
//			myNewFixedThreadPool(3, blockState, fileNameWithPosfix, urlString, persistFILEURI);
			myNewFixedThreadPoolCallable(3, blockState, fileNameWithPosfix, urlString, persistFILEURI);
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
	}
	static void myNewFixedThreadPoolCallable (int numOfThreads, final BlockState blockState, 
			String fileNameWithPosfix, String urlString, String persistFILEURI) throws IOException{
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(numOfThreads);
		final long sizeOfBlock = 4 << 20;
		final InputStream[] inputStreams = new InputStream[numOfThreads];
		final RandomAccessFile[] randomAccessFiles = new RandomAccessFile[numOfThreads];
		for (int index = 0; index < numOfThreads; index ++) {
			URLConnection urlConnection = new URL(urlString).openConnection();
			urlConnection.connect();
			inputStreams[index] = urlConnection.getInputStream();
			randomAccessFiles[index] = new RandomAccessFile(persistFILEURI, "rw");
		}
		final long[] arrayOfStarts = new long[numOfThreads];
		Arrays.fill(arrayOfStarts, -1);
		final LinkedList<FutureTask<Integer>> linkedList = new LinkedList<FutureTask<Integer>>();
		for (int index = 0; index < blockState.getSizeOfIsFinished(); index ++) {
			final long start = index * sizeOfBlock;
			final long end = (index == blockState.getSizeOfIsFinished() - 1 ? blockState.getLengthOfFile() : (index+1)*sizeOfBlock) - 1;
			final int indexBlockState = index;
			URLConnection urlConnection = new URL(urlString).openConnection();
			urlConnection.connect();
			linkedList.add(new FutureTask<>(new DownloadBlockCallable(inputStreams, randomAccessFiles, arrayOfStarts, start, end)));
		}
		Iterator<FutureTask<Integer>> iterator = linkedList.iterator();
		while (iterator.hasNext()) {
			fixedThreadPool.execute(iterator.next());
		}
		fixedThreadPool.shutdown();
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
	static class DownloadBlockCallable implements Callable<Integer> {
		final InputStream[] inputStreams;
		final RandomAccessFile[] randomAccessFiles;
		final long[] arrayOfStarts;
		final long start, end;
		public DownloadBlockCallable(InputStream[] inputStreams, RandomAccessFile[] randomAccessFiles
				, long[] arrayOfStarts, long start, long end) {
			this.inputStreams = inputStreams;
			this.randomAccessFiles = randomAccessFiles;
			this.arrayOfStarts = arrayOfStarts;
			this.start = start;
			this.end = end;
		}
		@Override
		public Integer call() throws Exception {
			long downloadLength = 0;
			byte[] buf = new byte[1024];
			int indexInThread = getValueFromString(Thread.currentThread().getName());
			try {
				long forSkip = arrayOfStarts[indexInThread] == -1 ? start : start - arrayOfStarts[indexInThread];
				inputStreams[indexInThread].skip(forSkip);
				randomAccessFiles[indexInThread].seek(start);
				int count = 0;
				long record = start;
				while( (count = inputStreams[indexInThread].read(buf)) > 0) {
					record += count;
					downloadLength += count;
					try {
						if (record <= end)
							randomAccessFiles[indexInThread].write(buf, 0, count);
						else {
							randomAccessFiles[indexInThread].write(buf, 0, count);
							System.out.println(indexInThread + " breaked");
							break;
						}
					} catch (java.lang.IndexOutOfBoundsException e) {
						System.out.println("IndexOutOfBoundsException : " + 
									"record : " + record + " end : " + end + " count : " + count);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				System.out.println("ThreadName : "+indexInThread + 
						" ended.\nDownload length is " + downloadLength);
				arrayOfStarts[indexInThread] = end;
			}
			return 0;
		}
	}
	static void myNewFixedThreadPool(int numOfThreads, final BlockState blockState, String fileNameWithPosfix, String urlString, String persistFILEURI) throws MalformedURLException, IOException {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(numOfThreads);
		final long sizeOfBlock = 4 << 20;
		final InputStream[] inputStreams = new InputStream[numOfThreads];
		final RandomAccessFile[] randomAccessFiles = new RandomAccessFile[numOfThreads];
		for (int index = 0; index < numOfThreads; index ++) {
			URLConnection urlConnection = new URL(urlString).openConnection();
			urlConnection.connect();
			inputStreams[index] = urlConnection.getInputStream();
			randomAccessFiles[index] = new RandomAccessFile(persistFILEURI, "rw");
		}
		final long[] arrayOfStarts = new long[numOfThreads];
		Arrays.fill(arrayOfStarts, -1);
		for (int index = 0; index < blockState.getSizeOfIsFinished(); index ++) {
			final long start = index * sizeOfBlock;
			final long end = (index == blockState.getSizeOfIsFinished() - 1 ? blockState.getLengthOfFile() : (index+1)*sizeOfBlock) - 1;
			final int indexBlockState = index;
			URLConnection urlConnection = new URL(urlString).openConnection();
			urlConnection.connect();
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					long downloadLength = 0;
					byte[] buf = new byte[1024];
					int indexInThread = getValueFromString(Thread.currentThread().getName());
					try {
						long forSkip = arrayOfStarts[indexInThread] == -1 ? start : start - arrayOfStarts[indexInThread];
						inputStreams[indexInThread].skip(forSkip);
						randomAccessFiles[indexInThread].seek(start);
						int count = 0;
						long record = start;
						while( (count = inputStreams[indexInThread].read(buf)) > 0) {
							record += count;
							downloadLength += count;
							try {
								if (record <= end)
									randomAccessFiles[indexInThread].write(buf, 0, count);
								else {
									randomAccessFiles[indexInThread].write(buf, 0, count);
									System.out.println(indexInThread + " breaked");
									break;
								}
							} catch (java.lang.IndexOutOfBoundsException e) {
								System.out.println("IndexOutOfBoundsException : " + 
											"record : " + record + " end : " + end + " count : " + count);
							}
						}
						blockState.setTrueInIndex(indexBlockState);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
//						try {
//							if (null != inputStreams[indexInThread])
//								inputStreams[indexInThread].close();
//							if (null != randomAccessFiles[indexInThread])
//								randomAccessFiles[indexInThread].close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
						System.out.println("ThreadName : "+indexInThread + 
								" ended.\nDownload length is " + downloadLength);
						arrayOfStarts[indexInThread] = end;
					}
				}
			});
//			try {
//				if (null != inputStreams[index])
//					inputStreams[index].close();
//				if (null != randomAccessFiles[index])
//					randomAccessFiles[index].close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
		fixedThreadPool.shutdown();
		if ( fixedThreadPool.isShutdown() ) {
			System.out.println("下载完成");
		}
		
//		for (int index = 0; index < numOfThreads; index ++) {
//			inputStreams[index].close();
//			randomAccessFiles[index].close();
//		}
	}
	private synchronized static boolean getBlockStateNow(String fileNameWithPosfix, int index) {
		BlockState blockStateNow = FileHelper.readBlockStateFromDisk(fileNameWithPosfix);
		return blockStateNow.getValueOfIsFinished(index);
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