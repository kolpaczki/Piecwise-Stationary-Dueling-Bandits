import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RUCB extends StationaryDuelingAlgorithm {

	private final double alpha;						// upper confidence bound regulator
	private int[][] wins;							// number of wins between arms 
	private double[][] u;							// upper confidence bounds for pairs
	private int t;									// current time step
	private int firstArm;							// last first arm played
	private int secondArm;							// last second arm played
	private final Random random = new Random();
	
	public RUCB(int K, double alpha) {
		super(K);
		this.alpha = alpha;
		if (alpha < 0)
			System.out.println("ERROR: alpha must not be negative!");
	}

	@Override
	public void initialize() {
		wins = new int[K][K];
		u = new double[K][K];
		t = 1;
	}

	@Override
	public int[] getPair() {
		// updates upper confidence bounds
		double c = Math.sqrt(alpha * Math.log(t));
		for (int i = 0; i < K; i++) {
			for (int j = i+1; j < K; j++) {
				double w = wins[i][j] + wins[j][i];
				double d = c / Math.sqrt(w);
				if (w == 0) {
					u[i][j] = 2.0;
					u[j][i] = 2.0;
				} else {
					u[i][j] = wins[i][j] / w + d;
					u[j][i] = wins[j][i] / w + d;
				}
			}
			u[i][i] = 0.5;
		}
		
		// chooses the first arm
		List<Integer> candidates = new ArrayList<Integer>();
		for (int i = 0; i < K; i++) {
			candidates.add(i);
			for (int j = 0; j < K; j++) {
				if (u[i][j] < 0.5) {
					candidates.remove((Integer) i);
					break;
				}
			}
		}
		firstArm = candidates.isEmpty() ? random.nextInt(K) : candidates.get(random.nextInt(candidates.size()));
		
		// chooses the second arm
		secondArm = 0;
		for (int i = 1; i < K; i++) {
			if (u[i][firstArm] > u[secondArm][firstArm])
				secondArm = i;
		}

		int pair[] = {firstArm, secondArm};
		return pair;
	}

	@Override
	public void update(boolean firstWon) {
		// updates the wins
		if (firstWon)
			wins[firstArm][secondArm]++;
		else
			wins[secondArm][firstArm]++;
		t++;
	}

	@Override
	public String toString() {
		return "RUCB" + String.valueOf(alpha).replace('.', ',');
	}

	@Override
	public int getSuspectedCondorcetWinner() {
		return firstArm;
	}
	
	@Override
	public void update(int winner, int loser) {
		// updates the wins
		wins[winner][loser]++;
		t++;
	}

}