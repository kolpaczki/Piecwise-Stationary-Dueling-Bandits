public abstract class DuelingAlgorithm {
	
	protected final int K;	// number of arms
	
	public DuelingAlgorithm(int K) {
		this.K = K;
	}
	
	// initializes the algorithm so that it is ready to use, has to be done before each new use
	public abstract void initialize();
	
	// returns the pair to be played
	public abstract int[] getPair();
	
	// updates it internal state given whether the first arm of the last recent pair to be played has won
	public abstract void update(boolean firstWon);
	
	public abstract String toString();
	
}