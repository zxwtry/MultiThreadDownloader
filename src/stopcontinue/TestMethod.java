package stopcontinue;

import java.util.Scanner;

import util.SystemInfo;

public class TestMethod {
	public TestMethod(String url, String name) { /// xx/weblogic60b2_win.exe
		try {
			SiteInfoBean bean = new SiteInfoBean(url, SystemInfo.getDefaultDownloadPath(),
				name, 5);
			// SiteInfoBean bean = new
			// SiteInfoBean("http://localhost:8080/down.zip","L:\\temp","weblogic60b2_win.exe",5);
			SiteFileFetch fileFetch = new SiteFileFetch(bean);
			fileFetch.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please input the url : ");
		String url = scanner.nextLine();
		System.out.println("Please input the name : ");
		String name = scanner.nextLine();
		new TestMethod(url, name);
		scanner.close();
	}
}
