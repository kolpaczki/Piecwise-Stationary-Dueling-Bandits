public abstract class StationaryDuelingAlgorithm extends DuelingAlgorithm {

	public StationaryDuelingAlgorithm(int K) {
		super(K);
	}
	
	public abstract int getSuspectedCondorcetWinner();
	
	public abstract void update(int winner, int loser);

}