package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FileHelper {
	public static String getFileNameWithPosfixFromURL(String urlString) {
		StringBuilder stringBuilder = new StringBuilder(urlString);
		for (int index = 0; index < urlString.length(); index ++) {
			if (urlString.charAt(index) == '\\') {
				stringBuilder.setCharAt(index, '/');
			}
		}
		int lastSlashIndex = stringBuilder.lastIndexOf("/");
		String name = stringBuilder.substring(lastSlashIndex+1);
		return name;
	}
	public static long getLengthOfFile(String urlString) {
		long lengthOfFile = 0;
		try {
			URLConnection urlConnection = new URL(urlString).openConnection();
			urlConnection.connect();
			lengthOfFile = urlConnection.getContentLengthLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lengthOfFile;
	}
	public static BlockState getBlockState(String urlString) {
		return getBlockState(urlString, 4);
	}
	public static BlockState getBlockState(String urlString, int sizeOfM) {
		String fileNameWithPosfix = getFileNameWithPosfixFromURL(urlString);
		String mtdString = getPersistMTDURI(fileNameWithPosfix);
		BlockState blockState = null;
		blockState = readBlockStateFromDisk(mtdString);
		if (blockState != null)   return blockState;
		blockState = new BlockState();
		long lengthOfFile = FileHelper.getLengthOfFile(urlString);
		long lengthOfBlock = sizeOfM << 20;
		int numOfBlock = (int)(lengthOfFile / lengthOfBlock);
		blockState.setSizeOfIsFinised(numOfBlock);
		blockState.setLengthOfFile(lengthOfFile);
		persistBlockStateToDisk(mtdString, blockState);
		return blockState;
	}
	public static ArrayList<BlockState> readBlockStateFromDisk() {
		String pathBase = SystemInfo.getDefaultDownloadPath();
		File filePath = new File(pathBase);
		String[] filePaths = filePath.list();
		ArrayList<BlockState> blockStates = new ArrayList<BlockState>();
		for (String string : filePaths) {
			if (string.endsWith(".mtd")) {
				BlockState blockState = readBlockStateFromDisk(pathBase + "/" +string);
				if (blockState != null)
					blockStates.add(blockState);
			}
		}
		return blockStates;
	}
	public static BlockState readBlockStateFromDisk(String fileNameWithPosfix) {
		BlockState blockState = null;
		ObjectInputStream objectInputStream = null;
		final String persistURI = getPersistMTDURI(fileNameWithPosfix);
		try {
			File file = new File(persistURI);
			if (! file.exists()) {
				return null;
			}
			objectInputStream = new ObjectInputStream(new FileInputStream(file));
			blockState = (BlockState) objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != objectInputStream) {
				try {
					objectInputStream.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return blockState;
	}
	public static boolean persistBlockStateToDisk(String persistURI, BlockState blockState) {
		File file = new File(persistURI);
		System.out.println("persistURI : " + persistURI);
		ObjectOutputStream objectOutputStream = null;
		try {
			if (! file.exists())
				file.createNewFile();
			objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
			objectOutputStream.writeObject(blockState);
			objectOutputStream.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (null != objectOutputStream) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static String getPersistMTDURI(String fileNameWithPosfix) {
		return SystemInfo.getDefaultDownloadPath()+"/"+fileNameWithPosfix+".mtd";
	}
	public static String getPersistFILEURI(String fileNameWithPosfix) {
		String persistFILEURI = SystemInfo.getDefaultDownloadPath()+"/"+fileNameWithPosfix;
		File file = new File(persistFILEURI);
		int index = 1;
		while (file.exists()) {
			persistFILEURI = getPersistFILEURI(persistFILEURI, index ++);
			file = new File(persistFILEURI);
		}
		
		return persistFILEURI;
	}
	private static String getPersistFILEURI(String persistFILEURI ,int index) {
		int lastDotIndex = persistFILEURI.lastIndexOf('.');
		StringBuilder stringBuilder = new StringBuilder(persistFILEURI);
		stringBuilder.insert(lastDotIndex, "-"+index);
		return stringBuilder.toString();
	}
	
}
