import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BeatTheWinner extends StationaryDuelingAlgorithm {
	
	private int round;
	private List<Integer> queue;		// queue of arms
	private int incumbent;				// incumbent of the current round
	private int challenger;				// challenger of the current round
	private int incumbentWins;			// number of times the incumbent has won in the current round
	private int challengerWins;			// number of times the challenger has won the current round
	private final boolean reset;		// round number resets if true and challenger wins the round
	
	public BeatTheWinner(int K, boolean reset) {
		super(K);
		this.reset = reset;
	}

	@Override
	public void initialize() {
		round = 1;
		
		// puts all arms in random order into the queue
		queue = new ArrayList<Integer>();
		List<Integer> remaining = new ArrayList<Integer>();
		for (int i = 0; i < K; i++)
			remaining.add(i);
		Random random = new Random();
		for (int i = 0; i < K; i++) {
			int arm = remaining.get(random.nextInt(remaining.size()));
			remaining.remove((Integer) arm);
			queue.add(arm);
		}
		
		// sets the incumbent and challenger and remove sthem from the queue
		incumbent = queue.get(0);
		queue.remove(0);
		challenger = queue.get(0);
		queue.remove(0);
		
		
		incumbentWins = 0;
		challengerWins = 0;
	}

	@Override
	public int[] getPair() {
		int[] pair = {incumbent, challenger};
		return pair;
	}

	@Override
	public void update(boolean firstWon) {
		// updates the number of wins
		if (firstWon)
			incumbentWins++;
		else
			challengerWins++;
		
		// checks if the round is finished and updates the queue, incumbent and challenger
		if (incumbentWins == round || challengerWins == round) {
			int winner = incumbentWins == round ? incumbent : challenger;
			int loser = incumbent + challenger - winner;
			round = reset && incumbent == loser ? 1 : round + 1;
			incumbentWins = 0;
			challengerWins = 0;
			incumbent = winner;
			queue.add(loser);
			challenger = queue.get(0);
			queue.remove(0);
		}
	}

	@Override
	public String toString() {
		return "BTW" + (reset ? "R" : "");
	}

	@Override
	public int getSuspectedCondorcetWinner() {
		return incumbent;
	}

	@Override
	public void update(int winner, int loser) {}
	
	public int getRound() {
		return round;
	}

}