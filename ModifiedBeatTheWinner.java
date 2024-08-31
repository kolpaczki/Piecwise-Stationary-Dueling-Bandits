import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModifiedBeatTheWinner extends StationaryDuelingAlgorithm {
	
	private int[] r;								// r values for each arm
	private int host;								// current host
	private int opponent;							// current opponent
	private int hostWins;							// number of times the host has won in the current round
	private int opponentWins;						// number of times the opponent has won in the current round
	private List<Integer> arms;						// list of all arms
	private int sum;								// sum of all r values
	private final Random random = new Random();
	
	public ModifiedBeatTheWinner(int K) {
		super(K);
	}

	@Override
	public void initialize() {
		// initialize r values
		r = new int[K];
		for (int i = 0; i < K; i++)
			r[i] = 1;
		sum = K;
		
		// initializes host, opponent and list of arm
		arms = new ArrayList<Integer>();
		for (int i = 0; i < K; i++)
			arms.add(i);
		host = random.nextInt(K);
		arms.remove(host);
		opponent = arms.get(random.nextInt(K-1));
		arms.add(host, host);
		hostWins = 0;
		opponentWins = 0;
	}

	@Override
	public int[] getPair() {
		int[] pair = {host, opponent};
		return pair;
	}

	@Override
	public void update(boolean firstWon) {
		// update win numbers
		if (firstWon)
			hostWins++;
		else
			opponentWins++;
		
		// check if the round is over
		if (hostWins == r[host] || opponentWins == r[host]) {
			int winner = hostWins == r[host] ? host : opponent;
			int loser = hostWins == r[host] ? opponent : host;
			// update r values and their sum 
			r[winner]++;
			sum++;
			sum -= r[loser];
			r[loser] = Math.max(r[loser]-1,1);
			sum += r[loser];
			host = winner;
			hostWins = 0;
			opponentWins = 0;
			
			// choose randomly the next opponent given their r values
			arms.remove(host);
			sum -= r[host];
			double rand = random.nextDouble() * sum;
			for (int i = 0; i < K-1; i++) {
				if (rand <= r[arms.get(i)]) {
					opponent = arms.get(i);
					break;
				} else
					rand -= r[arms.get(i)];
			}
			arms.add(host, host);
			sum += r[host];
		}
	}

	@Override
	public String toString() {
		return "MBTW";
	}

	@Override
	public int getSuspectedCondorcetWinner() {
		return host;
	}

	@Override
	public void update(int winner, int loser) {}

}