package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BlockStateTestTest {
	public static void main(String[] args) {
//		saveTest();
		readTest("C:/data/state.txt");
	}
	static void readTest(String url) {
		File file = new File(url);
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(new FileInputStream(file));
			BlockStateTest blockStateTest = (BlockStateTest) objectInputStream.readObject();
			System.out.println(blockStateTest.getValueOfIsFinished(0));
			System.out.println(blockStateTest.getValueOfIsFinished(10));
			System.out.println(blockStateTest.getValueOfIsFinished(14));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != objectInputStream)
					objectInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	static void saveTest() {
		BlockStateTest blockStateTest = new BlockStateTest();
		blockStateTest.setSizeOfIsFinised(14);
		blockStateTest.setTrueInIndex(10);
		save(blockStateTest, "C:/data/state.txt");
	}
	static void save(BlockStateTest blockStateTest, String url) {
		File file = new File(url);
		ObjectOutputStream objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
			objectOutputStream.writeObject(blockStateTest);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != objectOutputStream)
					objectOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
