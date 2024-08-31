import java.util.ArrayList;
import java.util.List;

public class DGIM extends Window {
	
	private final int r;												// r or r-1 buckets of the same size except for the smallest and biggest size are maintained 
	private final List<Bucket> buckets = new ArrayList<Bucket>();
	private int t = 0;
	
	public DGIM(int length, double epsilon) {
		super(length);
		r = Math.max(2, (int) Math.ceil(1.0 + 1.0 / epsilon));
	}
	
	@Override
	public void push(Boolean element) {
		t++;
		
		// removes the oldest bucket if it is out of the window length
		if (buckets.size() > 0 && buckets.get(buckets.size()-1).getTimestamp() <= t-length)
			buckets.remove(buckets.size()-1);
		
		// adds a new bucket of size 1 and checks if buckets are to be merged
		if (element) {
			buckets.add(0, new Bucket(1, t));
			check(0);
		}
	}
	
	// checks recursively if buckets are to be merged
	private void check(int pos) {
		if (pos >= buckets.size())
			return;
		
		// counts the number of bcukets with same soze as the one at position pos in the list
		int size = buckets.get(pos).getSize();
		int n = 0;
		for (; pos < buckets.size() && buckets.get(pos).getSize() == size; pos++)
			n++;
		
		// merges last two buckets if necessary and continues to check the next size
		if (n > r) {
			pos--;
			Bucket b = buckets.get(pos).merge(buckets.get(pos-1));
			buckets.remove(pos);
			buckets.remove(pos-1);
			buckets.add(pos-1, b);
			check(pos-1);
		}
	}
	
	@Override
	public void clear() {
		buckets.clear();
		t = 0;
	}
	
	@Override
	public boolean isFilled() {
		return t >= length;
	}
	
	@Override
	public int getFirstSum() {
		return getSum(length/2);
	}
	
	@Override
	public int getSecondSum() {
		return getSum(length) - getSum(length/2);
	}
	
	// returns the sum of 1's at the k most recent bits using DGIM
	private int getSum(int k) {
		if (buckets.size() == 0)
			return 0;
		
		int pos = 0;
		int sum = 0;
		
		// sums up the sizes of buckets containing the k most recent bits
		for (; pos < buckets.size() && buckets.get(pos).getTimestamp() > t-k; pos++)
			sum += buckets.get(pos).getSize();
		
		// removes half of the last bucket
		if (pos > 0)
			sum -= buckets.get(pos-1).getSize() / 2;
		
		return sum;	
	}
	
}