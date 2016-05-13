package bac;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.BlockState;
import util.FileHelper;
import util.SystemInfo;

public class Main {
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
		final InputStream[] inputStreams = new InputStream[blockState.getSizeOfIsFinished()];
		final RandomAccessFile[] randomAccessFiles = new RandomAccessFile[blockState.getSizeOfIsFinished()];
		for (int index = 0; index < blockState.getSizeOfIsFinished(); index ++) {
			URLConnection urlConnection = new URL(urlString).openConnection();
			urlConnection.connect();
		    final InputStream inputStream = urlConnection.getInputStream();
		    inputStreams[index] = inputStream;
			@SuppressWarnings("resource")
			final RandomAccessFile randomAccessFile = new RandomAccessFile(SystemInfo.getDefaultDownloadPath()+"/"+uri, "rw");
			final int indexInThread = index;
			final long start = index * sizeOfBlock;
			final long end = (index == blockState.getSizeOfIsFinished() - 1 ? blockState.getLengthOfFile() : (index+1)*sizeOfBlock) - 1;
			randomAccessFiles[index] = randomAccessFile;
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					long downloadLength = 0;
					byte[] buf = new byte[1024];
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
	}
}