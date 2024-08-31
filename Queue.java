import java.util.ArrayList;
import java.util.List;

public class Queue {
	
	private int sum = 0; 											// sum of all bits in the queue
	private final List<Boolean> list = new ArrayList<Boolean>();	
	
	// pushes the bit into the queue
	public void push(boolean element) {
		list.add(element);
		sum = element ? sum+1 : sum;  
	}
	
	// removes and returns the oldest bit from the queue
	public boolean pop() {
		sum = list.get(0) ? sum-1 : sum;
		return list.remove(0);
	}
	
	// resets the queue
	public void clear() {
		list.clear();
		sum = 0;
	}
	
	public int size() {
		return list.size();
	}
	
	public int getSum() {
		return sum;
	}
	
}