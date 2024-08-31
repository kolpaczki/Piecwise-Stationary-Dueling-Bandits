public class BeatTheWinnerResetStrong extends NonstationaryDuelingAlgorithm {

	private final BeatTheWinner btw = new BeatTheWinner(K, true);	// used instance of Winner Stays
	private final double beta;										// regulator of the length of exploitation phases
	private int l;													// current round
	private boolean exploit;										// true if in exploitation phase
	private int steps;												// number of time steps left in current exploitation phase
	
	public BeatTheWinnerResetStrong(int K, double beta) {
		super(K);
		this.beta = beta;
	}

	@Override
	public void initialize() {
		btw.initialize();
		l = 1;
		exploit = false;
	}

	@Override
	public int[] getPair() {
		if (exploit) {
			// plays only the suspected condorcet winner by Beat the Winner if in exploitation phase
			steps--;
			int[] pair = {btw.getSuspectedCondorcetWinner(), btw.getSuspectedCondorcetWinner()};
			return pair;
		} else {
			if (btw.getRound() == l)
				return btw.getPair();
			else {
				// starts the exploitation phase if Winner Stays has completed its round 
				steps = (int) Math.floor(Math.pow(beta, l));
				exploit = true;
				l++;
				return getPair();
			}
		}
	}

	@Override
	public void update(boolean firstWon) {
		// update Winner Stays if not in exploitation phase
		if (!exploit)
			btw.update(firstWon);
		else if (steps == 0)
			exploit = false;
	}

	@Override
	public String toString() {
		return "BTWRS" + String.valueOf(beta).replace('.', ',');
	}

	@Override
	public int getWindowSpan() {
		return 0;
	}

	@Override
	public void printParameters() {}
	
}