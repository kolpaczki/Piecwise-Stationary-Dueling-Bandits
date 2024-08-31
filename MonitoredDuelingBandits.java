import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonitoredDuelingBandits extends NonstationaryDuelingAlgorithm {

	private final StationaryDuelingAlgorithm alg;		// used Dueling algorithm
	private final double gamma;							// change point detection exploration factor
	private final int w;								// window length
	private final double b;								// threshold for changepoint detection
	private int t;										// current timestep reseted at detected changepoints
	private List<int[]> pairs;							// list of all pairs randomly ordered
	private boolean detect;								// true if in exploration step
	private int firstArm;								// last first arm played
	private int secondArm;								// last second arm played
	private Window[][] windows;							// detection windows for each pair of arms
	private final int numberOfPairs = K * (K-1) / 2;	// total number of pairs
	private final int divisor;							// divisor for exploration calculation
	private final double error;							// DGIM error to be used
	private final boolean observationTransfer;			// observation from changepoint detection exploration will be transfered to dueling algorithm if true
	private final boolean heuristicParameters;
	
	public MonitoredDuelingBandits(int K, double gamma, int w, double b, StationaryDuelingAlgorithm alg, double error, boolean observationTransfer) {
		super(K);
		this.gamma = gamma;
		this.w = w;
		this.b = b;
		this.alg = alg;
		this.error = error;
		divisor = (int) Math.floor(numberOfPairs / gamma);
		this.observationTransfer = observationTransfer;
		this.heuristicParameters = false;
	}
	
	public MonitoredDuelingBandits(int K, int T, int M, double delta, StationaryDuelingAlgorithm alg, double error, boolean observationTransfer, boolean heuristicParameters) {
		super(K);
		this.heuristicParameters = heuristicParameters;
		double errorTerm = heuristicParameters ? 1.0 : 1.0+2.0*error+5.0*error*error;
		double C = Math.log((4.0 * T + 2.0) * Math.sqrt(T) / (Math.sqrt(M)*K));
		double denominator = heuristicParameters ? Math.pow(delta, 2) :  Math.pow((1.0+error)*delta-8.0*error, 2);
		int preW = (int) Math.ceil(8.0 * errorTerm * C / denominator);
		w = preW % 2 == 0 ? preW : preW + 1;
		b = Math.sqrt(errorTerm * w / 2.0 * C) + (heuristicParameters ? 0.0 : 2.0*w*error);
		gamma = Math.sqrt(1.0 * M * w / T) * (K-1.0) / 2.0;
		this.alg = alg;
		this.error = error;
		divisor = (int) Math.floor(numberOfPairs / gamma);
		this.observationTransfer = observationTransfer;
	}

	@Override
	public void initialize() {
		alg.initialize();
		t = 1;
		resets.clear();
		
		// fills the list of all pairs randomly
		pairs = new ArrayList<int[]>();
		List<int[]> pairs2 = new ArrayList<int[]>();
		for (int i = 0; i < K-1; i++) {
			for (int j = i+1; j < K; j++) {
				int[] pair = {i,j};
				pairs2.add(pair);
			}
		}
		Random random = new Random();
		while (!pairs2.isEmpty()) {
			int[] pair = pairs2.get(random.nextInt(pairs2.size()));
			pairs2.remove(pair);
			pairs.add(pair);
		}
		
		// initializes DGIM instances if error is greater 0
		if (error == 0) {
			windows = new DoubleQueue[K][K];
			for (int i = 0; i < K; i++) {
				for (int j = i+1; j < K; j++)
					windows[i][j] = new DoubleQueue(w);
			}
		} else {
			windows = new DGIM[K][K];
			for (int i = 0; i < K; i++) {
				for (int j = i+1; j < K; j++)
					windows[i][j] = new DGIM(w, error);
			}
		}
	}

	@Override
	public int[] getPair() {
		int r = (t-1) % divisor;
		detect = r < numberOfPairs;
		// returns the pair played by the dueling algorithm if not in exploration step
		if (detect) {
			firstArm = pairs.get(r)[0];
			secondArm = pairs.get(r)[1];
			int[] pair = {firstArm, secondArm};
			return pair;
		} else
			return alg.getPair();
	}

	@Override
	public void update(boolean firstWon) {
		t++;
		// updates the dueling algorithm if not in exploration step
		if (detect) {
			alg.update(firstWon ? firstArm : secondArm, firstWon ? secondArm : firstArm);
			Window window = windows[firstArm][secondArm];
			window.push(firstWon);
			// checks if the the detection window is filled and the threshold exceeded
			if (window.isFilled() && Math.abs(window.getFirstSum() - window.getSecondSum()) > b) {
				// adds the reset to the histogram
				int reset = resets.size() == 0 ? t-1: t-1 + resets.get(resets.size()-1);
				resets.add(reset);
				hist.add(reset);
				
				// initializes the algorithm and the windows
				t=1;
				alg.initialize();
				for (int i = 0; i < K-1; i++) {
					for (int j = i+1; j < K; j++)
						windows[i][j].clear();
				}
			}
		} else
			alg.update(firstWon);
	}

	@Override
	public String toString() {
		return "MDB" + (observationTransfer ? "T " : " ") + "E" + (heuristicParameters ? "H" : "")  + String.valueOf(error).replace(".", ",") + " " + alg;
	}

	@Override
	public void printParameters() {
		System.out.println("gamma: " + gamma);
		System.out.println("w: " + w);
		System.out.println("b: " + b);
	}

	@Override
	public int getWindowSpan() {
		return  (w-1) * (int) (Math.floor( K * (K-1.0) / (2.0 * gamma))) + 1;
	}
	
}