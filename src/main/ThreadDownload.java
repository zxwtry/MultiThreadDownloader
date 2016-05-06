package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class ThreadDownload extends Thread{
	private long start;
	private long end;
	private InputStream inputStream;
	private RandomAccessFile randomAccessFile;
	private final int BUF_LEN = 1024;
	private byte[] buf = new byte[BUF_LEN];
	public ThreadDownload(long start, long end, InputStream inputStream, RandomAccessFile randomAccessFile) {
		System.out.println("ThreadName : " + this.getName() + " download from " + 
					start + " to " + end + " is downloading");
		this.start = start;
		this.end = end;
		this.inputStream = inputStream;
		this.randomAccessFile = randomAccessFile;
	}
	
	@Override
	public void run() {
		int times = 0;
		try {
			inputStream.skip(start);
			randomAccessFile.seek(start);
			int count = 0;
			long record = start;
			while( (count = inputStream.read(buf)) > 0) {
				record += count/8;
				try {
					if (record <= end)
						randomAccessFile.write(buf, 0, count);
					else {
						randomAccessFile.write(buf, 0, count);
						System.out.println(this.getName() + " breaked");
						break;
					}
				} catch (java.lang.IndexOutOfBoundsException e) {
					System.out.println("IndexOutOfBoundsException : " + 
								"record : " + record + " end : " + end + " count : " + count);
				}
				times ++;
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
			System.out.println("ThreadName : "+this.getName() + 
					" ended.\nDownload buffer times is " + times);
		}
	}
}