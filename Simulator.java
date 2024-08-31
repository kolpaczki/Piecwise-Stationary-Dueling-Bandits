import java.util.ArrayList;
import java.util.List;

public class Simulator {
	
	final static int T = 1000000;													// time horizon
	final static int K = 10;														// number of arms
	final static int M = 1;															// number of stationary segment
	final static double PMIN = 0.55;													// minimum probability of the condorcet winner to win against any arm
	final static double DELTA = 0.3;												// minimum absolute change at a changepoint
	final static boolean DELTAATWINNER = true;										// delta occurs at a winning probability of the condorcet winner if true
	final static double EPSILON = 0.0;												// error rate of DGIM
	//final static int MINDISTANCE = (int) Math.log(T);								// minimum distance between two changepoints
	
	final static int SCENARIOS = 10;												// number of scenarios
	final static int REPETITIONS = 10;												// number each scenario is repeated
	final static int RECORDPOINTSNUMBER = 5*M;										// number of data points
	final static int[] RECORDPOINTS = generateRecordPoints(T/RECORDPOINTSNUMBER);	// timesteps of data points
	
	final static double RUCBALPHA = 1.0;											// alpha of RUCB
	final static double WSSBETA = 1.05;												// beta of Winner Stays Strong
	final static double BTWRSBETA = 1.05;											// beta of Beat the Winner Reset Strong
	
	final static boolean PRINTWEAK = true;											// write weak regret into file if true
	final static boolean WRITETOFILE = false;										// write data points to file if true
	
	final static List<StationaryDuelingAlgorithm> stationaryAlgorithms = new ArrayList<StationaryDuelingAlgorithm>();				// list of stationary algorithms to be simulated
	final static List<NonstationaryDuelingAlgorithm> nonstationaryAlgorithms = new ArrayList<NonstationaryDuelingAlgorithm>();		// list of nonstationary algorithms to be simulated
	final static List<Scenario> scenarios = new ArrayList<Scenario>();																// list of scenarios to be simulated
	final static List<Integer> changepoints = generateChangepoints();																// list of changepoints to be used for each scenario
	
	public static void main(String[] args) {
		System.out.println("START");
		
		// adds scenarios
		for (int i = 0; i < SCENARIOS; i++)
			scenarios.add(new Scenario(Tools.generatePrefrenceMatrices(K, M, PMIN, DELTA, DELTAATWINNER), changepoints)); 		
		
		// stationary algorithms for strong regret
		final RUCB rucb = new RUCB(K, RUCBALPHA);
		final WinnerStaysStrong wss = new WinnerStaysStrong(K, WSSBETA);
		
		// stationary algorithms for weak regret
		final WinnerStays ws = new WinnerStays(K);
		final BeatTheWinner btw = new BeatTheWinner(K, false);
		final BeatTheWinner btwReset = new BeatTheWinner(K, true);
		final ModifiedBeatTheWinner mbtw = new ModifiedBeatTheWinner(K);
		
		// nonstationary algorithms for strong regret
		final MonitoredDuelingBandits mdbrucb = new MonitoredDuelingBandits(K, T, M, DELTA, new RUCB(K, RUCBALPHA), EPSILON, false, false);
		final MonitoredDuelingBandits mdbrucbT = new MonitoredDuelingBandits(K, T, M, DELTA, new RUCB(K, RUCBALPHA), EPSILON, true, false);
		final MonitoredDuelingBandits mdbwss = new MonitoredDuelingBandits(K, T, M, DELTA, new WinnerStaysStrong(K, WSSBETA), EPSILON, false, false);
		final BeatTheWinnerResetStrong btwrs = new BeatTheWinnerResetStrong(K, BTWRSBETA);
		
		final MonitoredDuelingBandits mdbwssE001 = new MonitoredDuelingBandits(K, T, M, DELTA, new WinnerStaysStrong(K, WSSBETA), 0.01, false, false);
		final MonitoredDuelingBandits mdbwssE002 = new MonitoredDuelingBandits(K, T, M, DELTA, new WinnerStaysStrong(K, WSSBETA), 0.02, false, false);
		final MonitoredDuelingBandits mdbwssE005 = new MonitoredDuelingBandits(K, T, M, DELTA, new WinnerStaysStrong(K, WSSBETA), 0.05, false, false);
		
		final MonitoredDuelingBandits mdbwssEH005 = new MonitoredDuelingBandits(K, T, M, DELTA, new WinnerStaysStrong(K, WSSBETA), 0.05, false, true);
		final MonitoredDuelingBandits mdbwssEH01 = new MonitoredDuelingBandits(K, T, M, DELTA, new WinnerStaysStrong(K, WSSBETA), 0.1, false, true);
		final MonitoredDuelingBandits mdbwssEH02 = new MonitoredDuelingBandits(K, T, M, DELTA, new WinnerStaysStrong(K, WSSBETA), 0.2, false, true);
		final MonitoredDuelingBandits mdbwssEH05 = new MonitoredDuelingBandits(K, T, M, DELTA, new WinnerStaysStrong(K, WSSBETA), 1, false, true);
		
		// nonstationary algorithms for weak regret
		final ExploreThenExploit etebtw1 = new ExploreThenExploit(K, T, new BeatTheWinner(K, false), EPSILON, DELTA, PMIN, false, false);
		final ExploreThenExploit etebtw2 = new ExploreThenExploit(K, T, new BeatTheWinner(K, false), EPSILON, DELTA, PMIN, true, false);
		final ExploreThenExploit etembtw1 = new ExploreThenExploit(K, T, new ModifiedBeatTheWinner(K), EPSILON, DELTA, PMIN, false, false);
		final ExploreThenExploit etembtw2 = new ExploreThenExploit(K, T, new ModifiedBeatTheWinner(K), EPSILON, DELTA, PMIN, true, false);
		final ExploreThenExploit etews = getEtEWS(new WinnerStays(K));
		final ExploreThenExploit etews1 = new ExploreThenExploit(K, T, new WinnerStays(K), EPSILON, DELTA, PMIN, false, false);
		final ExploreThenExploit etews2 = new ExploreThenExploit(K, T, new WinnerStays(K), EPSILON, DELTA, PMIN, true, false);
		
		final ExploreThenExploit etews2E001 = new ExploreThenExploit(K, T, new WinnerStays(K), 0.01, DELTA, PMIN, true, false);
		final ExploreThenExploit etews2E002 = new ExploreThenExploit(K, T, new WinnerStays(K), 0.02, DELTA, PMIN, true, false);
		final ExploreThenExploit etews2E005 = new ExploreThenExploit(K, T, new WinnerStays(K), 0.05, DELTA, PMIN, true, false);
		
		final ExploreThenExploit etews2EH005 = new ExploreThenExploit(K, T, new WinnerStays(K), 0.05, DELTA, PMIN, true, true);
		final ExploreThenExploit etews2EH01 = new ExploreThenExploit(K, T, new WinnerStays(K), 0.1, DELTA, PMIN, true, true);
		final ExploreThenExploit etews2EH02 = new ExploreThenExploit(K, T, new WinnerStays(K), 0.2, DELTA, PMIN, true, true);
		final ExploreThenExploit etews2EH05 = new ExploreThenExploit(K, T, new WinnerStays(K), 1, DELTA, PMIN, true, true);
		
		
		// add stationary algorithms for strong regret
		//stationaryAlgorithms.add(rucb);
		//stationaryAlgorithms.add(wss);
		
		// add stationary algorithms for weak regret
		//stationaryAlgorithms.add(ws); // bad
		stationaryAlgorithms.add(btw);
		stationaryAlgorithms.add(btwReset);
		//stationaryAlgorithms.add(mbtw); // bad
		
		// add nonstationary algorithms for strong regret
		//nonstationaryAlgorithms.add(mdbrucbT);
		//nonstationaryAlgorithms.add(mdbwss);
		//nonstationaryAlgorithms.add(btwrs);
		//nonstationaryAlgorithms.add(mdbwssE001);
		//nonstationaryAlgorithms.add(mdbwssE002);
		//nonstationaryAlgorithms.add(mdbwssE005);
		
		// add nonstationary algorithms for weak regret
		//nonstationaryAlgorithms.add(etews1); // bad
		//nonstationaryAlgorithms.add(etews2);
		//nonstationaryAlgorithms.add(etebtw1);
		//nonstationaryAlgorithms.add(etebtw2); // bad
		//nonstationaryAlgorithms.add(etembtw1); // bad
		//nonstationaryAlgorithms.add(etembtw2);
		//nonstationaryAlgorithms.add(etews2E001);
		//nonstationaryAlgorithms.add(etews2E002);
		//nonstationaryAlgorithms.add(etews2E005);
		
		// print parameters of nonstationary algorithms
		for (int i = 0; i < nonstationaryAlgorithms.size(); i++) {
			System.out.println(nonstationaryAlgorithms.get(i));
			nonstationaryAlgorithms.get(i).printParameters();
			System.out.println();
		}
		
		// simulate
		Average[][][] averageRecords = simulateAlgorithms();
		
		// print data points of stationary algorithms
		System.out.println("average cumulative regrets:");
		for (int i = 0; i < stationaryAlgorithms.size(); i++) {
			for (int r = 0; r < RECORDPOINTS.length; r++)
				System.out.println(stationaryAlgorithms.get(i) + ", " + RECORDPOINTS[r] + ": binary weak regret: " + averageRecords[i][r][0].getAverage() + ", binary strong regret: " + averageRecords[i][r][1].getAverage());
			System.out.println();
		}
		
		// print data points of nonstationary algorithms
		for (int i = 0; i < nonstationaryAlgorithms.size(); i++) {
			for (int r = 0; r < RECORDPOINTS.length; r++)
				System.out.println(nonstationaryAlgorithms.get(i) + ", " + RECORDPOINTS[r] + ": binary weak regret: " + averageRecords[i + stationaryAlgorithms.size()][r][0].getAverage() + ", binary strong regret: " + averageRecords[i + stationaryAlgorithms.size()][r][1].getAverage());
			System.out.println();
		}
		
		// print cumulative regret for nonstationary algorithms
		for (int i = 0; i < stationaryAlgorithms.size(); i++) {
			int r = RECORDPOINTS.length-1;
			System.out.println(stationaryAlgorithms.get(i) + ": binary weak regret: " + averageRecords[i][r][0].getAverage() + ", binary strong regret: " + averageRecords[i][r][1].getAverage());
		}
		System.out.println();
		
		// print cumulative regret for nonstationary algorithms
		for (int i = 0; i < nonstationaryAlgorithms.size(); i++) {
			int r = RECORDPOINTS.length-1;
			System.out.println(nonstationaryAlgorithms.get(i) + ": binary weak regret: " + averageRecords[i + stationaryAlgorithms.size()][r][0].getAverage() + ", binary strong regret: " + averageRecords[i + stationaryAlgorithms.size()][r][1].getAverage());
		}
		System.out.println();
		
		// print average rests of nonstationary algorithms
		System.out.println("average resets:");
		for (int i = 0; i < nonstationaryAlgorithms.size(); i++)
			System.out.println(nonstationaryAlgorithms.get(i) + ": " + 1.0 * nonstationaryAlgorithms.get(i).getHist().getTotalNumber() / REPETITIONS / SCENARIOS);
		
		if (WRITETOFILE) {
			int regretType = PRINTWEAK ? 0 : 1;
			String regretName = PRINTWEAK ? "weak" : "strong";
			String parameters = "K" + K + " T" + T +  " M" + M + " S" + SCENARIOS + " R" + REPETITIONS + " D" + String.valueOf(DELTA).replace('.', ',') + " P" + String.valueOf(PMIN).replace('.', ',') + " E" + String.valueOf(EPSILON).replace('.', ',');
			// write data points of stationary algorithms
			for (int i = 0; i < stationaryAlgorithms.size(); i++) {
				double[] regret = new double[RECORDPOINTS.length];
				for (int j = 0; j < regret.length; j++)
					regret[j] = averageRecords[i][j][regretType].getAverage();
				Tools.writeToFile(stationaryAlgorithms.get(i) + " " + regretName + " " + parameters, regret, RECORDPOINTS);
			}
			// write data points and resets of nonstationary algorithms
			for (int i = 0; i < nonstationaryAlgorithms.size(); i++) {
				double[] regret = new double[RECORDPOINTS.length];
				for (int j = 0; j < regret.length; j++)
					regret[j] = averageRecords[i+stationaryAlgorithms.size()][j][regretType].getAverage();
				String fileNameBase = nonstationaryAlgorithms.get(i) + " " + regretName + " " + parameters;
				Tools.writeToFile(fileNameBase, regret, RECORDPOINTS);
				Tools.writeToFile(fileNameBase + " hist", nonstationaryAlgorithms.get(i).getHist());
				Tools.writeToFile(fileNameBase + " histCal", nonstationaryAlgorithms.get(i).getHist().getCalibrated(changepoints));
				double resets = (1.0 * nonstationaryAlgorithms.get(i).getHist().getTotalNumber()) / SCENARIOS / REPETITIONS;
				double falseAlarms = (1.0 * nonstationaryAlgorithms.get(i).getHist().getTotalFalseAlarms(changepoints, nonstationaryAlgorithms.get(i).getWindowSpan())) / SCENARIOS / REPETITIONS;
				double delay = nonstationaryAlgorithms.get(i).getHist().getAverageDelay(changepoints, nonstationaryAlgorithms.get(i).getWindowSpan());
				Tools.writeToFile(fileNameBase + " resets", resets, falseAlarms, delay);
			}
		}	
	}
	
	// simulate all algorithms
	private static Average[][][] simulateAlgorithms() {
		Average[][][] averageRecords = new Average[stationaryAlgorithms.size() + nonstationaryAlgorithms.size()][RECORDPOINTS.length][2];
		for (int i = 0; i < stationaryAlgorithms.size(); i++)
			averageRecords[i] = simulate(scenarios, stationaryAlgorithms.get(i));
		for (int i = 0; i < nonstationaryAlgorithms.size(); i++)
			averageRecords[i + stationaryAlgorithms.size()] = simulate(scenarios, nonstationaryAlgorithms.get(i));
		return averageRecords;
	}
	
	// simulate an algorithm
	private static Average[][] simulate(List<Scenario> scenarios, DuelingAlgorithm alg) {
		Average[][] averageRecords = new Average[RECORDPOINTS.length][2];
		for (int i = 0; i < RECORDPOINTS.length; i++) {
			averageRecords[i][0] = new Average();
			averageRecords[i][1] = new Average();
		}
		
		for (int s = 0; s < scenarios.size(); s++) {
			for (int r = 0; r < REPETITIONS; r++) {
				double[][] records = simulateScenario(scenarios.get(s), alg);
				for (int i = 0; i < RECORDPOINTS.length; i++) {
					averageRecords[i][0].add(records[i][0]);
					averageRecords[i][1].add(records[i][1]);
				}
				System.out.println(alg + ", scenario: " + (s+1) +", repetition: " + (r+1) + ", cumulative binary weak regret: " + records[RECORDPOINTS.length-1][0] + ", cumulative binary strong regret: " + records[RECORDPOINTS.length-1][1]);	
			}
		}
		
		return averageRecords;
	}
	
	// simulate a scenario
	private static double[][] simulateScenario(Scenario scenario, DuelingAlgorithm alg) {
		Environment environment = new Environment(scenario.getPreferenceMatrices(), scenario.getChangepoints());
		alg.initialize();
		double[][] records = new double[RECORDPOINTS.length][2];
		int record = 0;
		for (int t = 1; t <= T; t++) {
			int[] pair = alg.getPair();
			boolean firstWon = environment.processPair(pair[0], pair[1]);
			alg.update(firstWon);
			if (record < RECORDPOINTS.length && t == RECORDPOINTS[record]) {
				records[record][0] = environment.getBinaryWeakRegret();
				records[record][1] = environment.getBinaryStrongRegret();
				record++;
			}
		}
		return records;
	}
	
	// generate evenly spaced record points 
	private static int[] generateRecordPoints(int distance) {
		int[] points = new int[T / distance];
		for (int i = 1; i * distance <= T; i++)
			points[i-1] = i * distance;
		return points;
	}
	
	// generate evenly spaced changepoints
	private static List<Integer> generateChangepoints() {
		List<Integer> points = new ArrayList<Integer>();
		if (M==1)
			return points;
		for (int i = 1; i < M; i++)
			points.add((i * T) / M + 1);
		return points;
	} 
	
	// return EtE instance using Winner Stays with parameters from Corollary 4.18
	private static ExploreThenExploit getEtEWS(WinnerStays ws) {
		double errorTerm = 1 + 2*EPSILON+5*EPSILON*EPSILON;
		double r = Math.max(2.0, Math.ceil(Math.log(2.0*T*(1.0+(1.0-PMIN)/(2.0*PMIN-1.0)) / Math.log(T)) / Math.log(PMIN / (1.0-PMIN))));
		int tildeT = (int) (Math.pow(r, 3) * Math.pow(K, 3) * T / Math.log(T));
		int preW = (int) Math.ceil(errorTerm*(16*Math.log(T)+6) / Math.pow((1.0+EPSILON)*DELTA - 8.0*EPSILON, 2));
		int w = preW % 2 == 0 ? preW : preW+1;
		double b = Math.sqrt(errorTerm*w*(Math.log(T)+3.0/8.0)) + 2.0*w*EPSILON;
		return new ExploreThenExploit(K, tildeT, w, b, ws, EPSILON, PMIN);
	}
	
}