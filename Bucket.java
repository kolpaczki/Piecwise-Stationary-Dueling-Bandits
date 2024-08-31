public class Bucket {
		
	private final int size;
	private final int timestamp;
	
	public Bucket(int size, int timestamp) {
		this.size = size;
		this.timestamp = timestamp;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	
	// returns a merged bucket of doubled size and latest timestamp if both are of the same size
	public Bucket merge(Bucket b) {
		if (size != b.getSize())
			return null;
		
		return new Bucket(size*2, Math.max(timestamp, b.getTimestamp()));
	}
	
}