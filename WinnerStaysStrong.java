public class WinnerStaysStrong extends StationaryDuelingAlgorithm {

	private final WinnerStays ws = new WinnerStays(K);	// used instance of Winner Stays
	private final double beta;									// regulator of the length of exploitation phases
	private int l;												// current round
	private boolean exploit;									// true if in exploitation phase
	private int steps;											// number of time steps left in current exploitation phase
	
	public WinnerStaysStrong(int K, double beta) {
		super(K);
		this.beta = beta;
	}

	@Override
	public void initialize() {
		ws.initialize();
		l = 1;
		exploit = false;
	}

	@Override
	public int[] getPair() {
		if (exploit) {
			// plays only the suspected condorcet winner by Winner Stays if in exploitation phase
			steps--;
			int[] pair = {ws.getSuspectedCondorcetWinner(), ws.getSuspectedCondorcetWinner()};
			return pair;
		} else {
			if (ws.getRound() == l)
				return ws.getPair();
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
			ws.update(firstWon);
		else if (steps == 0)
			exploit = false;
	}

	@Override
	public String toString() {
		return "WSS" + String.valueOf(beta).replace('.', ',');
	}
	
	@Override
	public int getSuspectedCondorcetWinner() {
		return ws.getSuspectedCondorcetWinner();
	}
	
	@Override
	public void update(int winner, int loser) {}

}