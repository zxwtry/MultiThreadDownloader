package util;

import java.io.File;
import java.util.Properties;

public class SystemInfo {
	private static final String fileName = "mtdDownloads";
	private static Properties properties = System.getProperties();
	public static String getOSArch() {
		return properties.getProperty("os.arch");
	}
	public static String getOSName() {
		return properties.getProperty("os.name");
	}
	public static String getUserHome() {
		return properties.getProperty("user.home");
	}
	public static String getUserName() {
		return properties.getProperty("user.name");
	}
	public static String getDefaultDownloadPath() {
		String basePath = null;
		if (getOSName().equals("Linux")) {
			basePath = getUserHome()+"/"+fileName;
			File baseFile = new File(basePath);
			if (! baseFile.exists() ) {
				baseFile.mkdirs();
			}
		} else if (getOSName().substring(0, 7).equals("Windows")){
			basePath = "C:/"+fileName;
			File baseFile = new File(basePath);
			if (! baseFile.exists() ) {
				baseFile.mkdirs();
			}
		} else {
			basePath = getUserHome()+"/"+fileName;
			File baseFile = new File(basePath);
			if (! baseFile.exists() ) {
				baseFile.mkdirs();
			}
		}
		return basePath;
	}
}