package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainThreadDownload {
	public static void main(String[] args) throws MalformedURLException, IOException {
		String urlIndex = "http://dlsw.baidu.com/sw-search-sp"
				+ "/soft/3a/12350/QQ_8.2.17724.0_setup.1459155849.exe";
		URLConnection urlConnection = new URL(urlIndex).openConnection();
		InputStream inputStream = urlConnection.getInputStream();
		File file = new File("C:/data.exe");
		if (file.exists()) file.delete();
		file.createNewFile();
		OutputStream outputStream = new FileOutputStream(file);
		byte[] buf = new byte[4*1024*1024];
		int count = 0;
		long receiveAll = 0;
		while( (count = inputStream.read(buf)) != -1 ) {
			outputStream.write(buf, 0, count);
			receiveAll += count;
		}
		System.out.println(receiveAll);
		if (inputStream != null)	inputStream.close();
		if (outputStream != null) 	outputStream.close();
	}
}
