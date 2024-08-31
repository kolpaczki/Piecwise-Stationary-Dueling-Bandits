public class Average {
	
	private int n;				// number of data points
	private double average;		// average of data points	
	
	public Average() {
		reset();
	}
	
	// increments the number of data points and updates average
	public void add(double value) {
		average = (average * n + value) / (++n);
	}
	
	// resets the number of data points and average
	public void reset() {
		n = 0;
		average = 0;
	}
	
	public double getAverage() {
		return average;
	}
	
}