package bac;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.BlockState;
import util.FileHelper;
import util.SystemInfo;

public class Main2 {
	static Map<String, Integer> map = new HashMap<String, Integer>();
	static int valueCount = 0;
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String urlString = scanner.next();
		BlockState blockState = FileHelper.getBlockState(urlString);
		try {
			myNewFixedThreadPool(3, blockState, FileHelper.getFileNameFromURL(urlString), urlString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
	}
	static void myNewFixedThreadPool(int numOfThreads, BlockState blockState,String uri, String urlString) throws MalformedURLException, IOException {
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(numOfThreads);
		final long sizeOfBlock = 4 << 20;
		final InputStream[] inputStreams = new InputStream[numOfThreads];
		final RandomAccessFile[] randomAccessFiles = new RandomAccessFile[numOfThreads];
		for (int index = 0; index < numOfThreads; index ++) {
			URLConnection urlConnection = new URL(urlString).openConnection();
			urlConnection.connect();
			inputStreams[index] = urlConnection.getInputStream();
			System.out.println("is markSupported : "+inputStreams[index].markSupported());
			randomAccessFiles[index] = new RandomAccessFile(SystemInfo.getDefaultDownloadPath()+"/"+uri, "rw");
		}
		for (int index = 0; index < blockState.getSizeOfIsFinished(); index ++) {
			final long start = index * sizeOfBlock;
			final long end = (index == blockState.getSizeOfIsFinished() - 1 ? blockState.getLengthOfFile() : (index+1)*sizeOfBlock) - 1;
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					long downloadLength = 0;
					byte[] buf = new byte[1024];
					int indexInThread = getValueFromString(Thread.currentThread().getName());
					try {
						inputStreams[indexInThread].skip(start);
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
//		for (int index = 0; index < numOfThreads; index ++) {
//			inputStreams[index].close();
//			randomAccessFiles[index].close();
//		}
		System.out.println("下载完成");
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