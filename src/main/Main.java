package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String urlIndex = "http://dlsw.baidu.com/sw-search-sp"
				+ "/soft/3a/12350/QQ_8.2.17724.0_setup.1459155849.exe";
		urlIndex = urlIndex.replace('\\', '/');
		String[] urlIndexSplits = urlIndex.split("/");
		if (urlIndexSplits == null) {
			scanner.close();
			return;
		}
		String fileName = urlIndexSplits[urlIndexSplits.length-1];
		String fileNameAbsoulte = SystemInfo.getDefaultDownloadPath()+"/"+fileName;
		int indexOfAnotherFile = 1;
		File file = new File(fileNameAbsoulte);
		while ( file.exists() ) {
			int indexOfLastDot = fileName.lastIndexOf(".");
			String fileNamePre = fileName.substring(0, indexOfLastDot);
			String fileNamePos = fileName.substring(indexOfLastDot+1, fileName.length());
			fileNameAbsoulte = SystemInfo.getDefaultDownloadPath()+"/"+fileNamePre+indexOfAnotherFile+++"."+fileNamePos;
			file = new File(fileNameAbsoulte);
		}
		
		int numOfThreads = 5;
		InputStream[] inputStreamArrays = new InputStream[numOfThreads];
		RandomAccessFile[] randomAccessFilesArrays = new RandomAccessFile[numOfThreads];
		try {
			file.createNewFile();
			URL url = new URL(urlIndex);
			long urlFileLength = url.openConnection().getContentLengthLong();
			if (urlFileLength < 0) {
				System.out.println("urlIndex is wrong");
				scanner.close();
				return;
			}
			System.out.println("Size of " + url.getFile() + " : " + urlFileLength);
			inputStreamArrays[0] = url.openStream();
			randomAccessFilesArrays[0] = new RandomAccessFile(file, "rw");
			long bytePerThread = urlFileLength / numOfThreads;
			long left = urlFileLength % numOfThreads;
			if (0 == left)
				System.out.println("left is 0");
			for (int index = 0; index < numOfThreads; index ++) {
				if (0 != index) {
					inputStreamArrays[index] = url.openStream();
					randomAccessFilesArrays[index] = new RandomAccessFile(file, "rw");
				}
				if (numOfThreads-1 == index) {
					new ThreadDownload(index*bytePerThread, urlFileLength-1, 
							inputStreamArrays[index], randomAccessFilesArrays[index]).start();
				} else {
					new ThreadDownload(index*bytePerThread, (index+1)*bytePerThread,
							inputStreamArrays[index], randomAccessFilesArrays[index]).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		scanner.close();
	}
}