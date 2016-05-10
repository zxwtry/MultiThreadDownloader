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
		String urlString = "http://sw.bos.baidu.com/sw-search-sp/"
				+ "software/b144b091ef8/VMware-workstation_full_12.2.0.1269.exe";
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
		for (int index = 0; index < blockState.getSizeOfIsFinished(); index ++) {
			URLConnection urlConnection = new URL(urlString).openConnection();
			urlConnection.connect();
		    final InputStream inputStream = urlConnection.getInputStream();
			final RandomAccessFile randomAccessFile = new RandomAccessFile(SystemInfo.getDefaultDownloadPath()+"/"+uri, "rw");
			final int indexInThread = index;
			final long start = index * sizeOfBlock;
			final long end = (index == blockState.getSizeOfIsFinished() - 1 ? blockState.getLengthOfFile() : (index+1)*sizeOfBlock) - 1;
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					long downloadLength = 0;
					byte[] buf = new byte[1024];
					try {
						inputStream.skip(start);
						randomAccessFile.seek(start);
						int count = 0;
						long record = start;
						while( (count = inputStream.read(buf)) > 0) {
							record += count;
							downloadLength += count;
							try {
								if (record <= end)
									randomAccessFile.write(buf, 0, count);
								else {
									randomAccessFile.write(buf, 0, count);
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
						try {
							if (null != inputStream)
								inputStream.close();
							if (null != randomAccessFile)
								randomAccessFile.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.out.println("ThreadName : "+indexInThread + 
								" ended.\nDownload length is " + downloadLength);
					}
				}
			});
			try {
				if (null != inputStream)
					inputStream.close();
				if (null != randomAccessFile)
					randomAccessFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fixedThreadPool.shutdown();
	}
}