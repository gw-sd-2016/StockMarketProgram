package helpers;

public class IntegerPair {
	int startingIndex;
	int endingIndex;

	public IntegerPair(int start, int finish) {
		startingIndex = start;
		endingIndex = finish;
	}

	public int returnStart() {
		return startingIndex;
	}

	public int returnEnd() {
		return endingIndex;
	}
}
