public class DoubleQueue extends Window {

	private final Queue q1 = new Queue();	// queue of recent half of bits
	private final Queue q2 = new Queue();	// queue of othe rhalf of bits
	
	public DoubleQueue(int length) {
		super(length);
	} 
	
	@Override
	public void push(Boolean element) {
		// adds the bit to the first queue, pushes the last bit from the first queue to the second and removes the last bit from the second queue if necessary
		q1.push(element);
		if (q1.size() > length/2) {
			q2.push(q1.pop());
			if (q2.size() > length/2)
				q2.pop();
		}
	}
	
	@Override
	public void clear() {
		q1.clear();
		q2.clear();
	}
	
	@Override
	public boolean isFilled() {
		return q1.size() == length/2 && q2.size() == length/2;
	}
	
	@Override
	public int getFirstSum() {
		return q1.getSum();
	}
	
	@Override
	public int getSecondSum() {
		return q2.getSum();
	}
	
}