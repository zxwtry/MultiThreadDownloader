package main;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String urlIndex = "http://dlsw.baidu.com/sw-search-sp"
				+ "/soft/3a/12350/QQ_8.2.17724.0_setup.1459155849.exe";
		urlIndex = urlIndex.replace('\\', '/');
		String[] urlIndexSplits = urlIndex.split("/");
		if (urlIndexSplits != null) {
			String fileName = urlIndexSplits[urlIndexSplits.length-1];
			String fileNameAbsoulte = SystemInfo.getDefaultDownloadPath()+"/"+fileName;
			int index = 1;
			File file = new File(fileNameAbsoulte);
			while ( file.exists() ) {
				file = new File(fileNameAbsoulte+String.valueOf(index ++));
			}
			try {
				file.createNewFile();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		scanner.close();
	}
}