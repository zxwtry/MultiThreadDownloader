package test;

import java.io.Serializable;
import java.util.Arrays;

public class BlockStateTest implements Serializable{
	private static final long serialVersionUID = 1L;
	private boolean[] isFinished = null;
	public int getSizeOfIsFinished() {
		if (null == isFinished)
			return 0;
		return isFinished.length;
	}
	public boolean setSizeOfIsFinised(int size) {
		if (isFinished == null) {
			isFinished = new boolean[size];
			Arrays.fill(isFinished, false);
			return true;
		} else {
			return false;
		}
	}
	public boolean setTrueInIndex(int index) {
		if (index < 0 || isFinished == null || index >= isFinished.length) {
			return false;
		}
		isFinished[index] = true;
		return true;
	}
	public boolean getValueOfIsFinished(int index) {
		if (index < 0 || isFinished == null || index >= isFinished.length) {
			return false;
		}
		return isFinished[index];
	}
}
