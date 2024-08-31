import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WinnerStays extends StationaryDuelingAlgorithm {

	private int[] C;							// difference between wins and losses of an arm
	private List<Integer> remaining;			// list of arms left for current round
	private List<Integer> beaten;				// list of already beaten arms in current round
	private int incumbent;						// incumbent in current round
	private int challenger;						// challenger in current round
	private int round;							// current round
	private Random random = new Random();
	private int lastWinner;						// winner of the last round
	
	public WinnerStays(int K) {
		super(K);
	}

	@Override
	public void initialize() {
		C = new int[K];
		// sets remanining and beaten arms, incumbent and challenger
		remaining = new ArrayList<Integer>();
		for (int i = 0; i < K; i++)
			remaining.add(i);
		beaten = new ArrayList<Integer>();
		incumbent = random.nextInt(K);
		remaining.remove((Integer) incumbent);
		challenger = remaining.get(random.nextInt(K-1));
		remaining.remove((Integer) challenger);
		round = 1;
	}

	@Override
	public int[] getPair() {
		int[] pair = {incumbent, challenger};
		return pair;
	}
	
	@Override
	public void update(boolean firstWon) {
		// updates C values
		int winner = firstWon ? incumbent : challenger; 
		int loser = firstWon ? challenger : incumbent;
		C[winner]++;
		C[loser]--;
		
		// checks if the iteration is finished
		if (C[loser] == -round) {
			incumbent = winner;
			beaten.add(loser);
			// checks if the round is finished
			if (remaining.size() == 0) {
				round++;
				remaining = beaten;
				beaten = new ArrayList<Integer>();
				lastWinner = incumbent;
			}
			challenger = remaining.get(random.nextInt(remaining.size()));
			remaining.remove((Integer) challenger);
		}
	}

	@Override
	public String toString() {
		return "WS";
	}

	@Override
	public int getSuspectedCondorcetWinner() {
		return round == 1 ? incumbent : lastWinner;
	}
	
	public int getRound() {
		return round;
	}
	
	@Override
	public void update(int winner, int loser) {}

}