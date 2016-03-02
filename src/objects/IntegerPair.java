package objects;

public class IntegerPair {
	protected int startingIndex;
	protected int endingIndex;

	public IntegerPair(int start, int finish) {
		this.startingIndex = start;
		this.endingIndex = finish;
	}

	public int returnStart() {

		return startingIndex;
	}

	public int returnEnd() {

		return endingIndex;
	}
}
