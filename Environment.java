import java.util.List;

public class Environment {

	private final List<double[][]> preferenceMatrices;		// list of all preference matrices
	private final List<Integer> changepoints;				// list of all changepoints
	private double[][] preferenceMatrix;					// current preference matrix
	private int condorcetWinner;							// current condorcet winner
	private double binaryWeakRegret = 0;					// current cumulative binary weak regret
	private double binaryStrongRegret = 0;					// current cumulative strong regret
	private int t = 1;										// current timestep
	private int m = 0;										// current number of changepoints encountered
	
	public Environment(List<double[][]> preferenceMatrices, List<Integer> changepoints) {
		this.preferenceMatrices = preferenceMatrices;
		this.changepoints = changepoints;
		preferenceMatrix = preferenceMatrices.get(0);
		condorcetWinner = Tools.getCondorcetWinner(preferenceMatrix);
	}
	
	// returns whether the first arm or second arm wins in a random duel
	public boolean processPair(int firstArm, int secondArm) {
		// updates preference matrix and condorcet winner in case of a changepoint
		if (m < changepoints.size() && t == changepoints.get(m)) {
			m++;
			preferenceMatrix = preferenceMatrices.get(m);
			condorcetWinner = Tools.getCondorcetWinner(preferenceMatrix);
		}
		
		// updates timetsep and regret
		t++;
		updateRegret(firstArm, secondArm);
		
		// decides randomly which arm wins
		return Math.random() <= preferenceMatrix[firstArm][secondArm];
	}
	
	public double getBinaryWeakRegret() {
		return binaryWeakRegret;
	}
	
	public double getBinaryStrongRegret() {
		return binaryStrongRegret;
	}
	
	// updates regret measures
	private void updateRegret(int firstArm, int secondArm) {
		binaryWeakRegret += firstArm == condorcetWinner || secondArm == condorcetWinner ? 0 : 1;
		binaryStrongRegret += firstArm == condorcetWinner && secondArm == condorcetWinner ? 0 : 1;
	}
	
}