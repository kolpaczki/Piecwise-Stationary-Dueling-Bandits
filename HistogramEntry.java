public class HistogramEntry {
	
	private final int key;
	private int number = 1;
	
	public HistogramEntry(int key) {
		this.key = key;
	}
	
	public void add() {
		number++;
	}
	
	public int getKey() {
		return key;
	}
	
	public int getNumber() {
		return number;
	}
	
}