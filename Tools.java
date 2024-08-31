import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Tools {
	
	// generates preference matrices with:
	// K: number of arms
	// number: number of matrices
	// pmin: minimum probability of the condorcet winner to win against any arm
	// delta: minimum absolute change at a changepoint
	// deltaAtWinner: delta occurs at a winning probability of the condorcet winner if true
	public static List<double[][]> generatePrefrenceMatrices(int K, int number, double pmin, double delta, boolean deltaAtWinner) {
		List<double[][]> matrices = new ArrayList<double[][]>();
		if (number == 0)
			return matrices;
		
		// generates first matrix
		Random random = new Random();
		int lastWinner = random.nextInt(K);
		matrices.add(generatePreferenceMatrix(K, lastWinner, pmin));
		
		// generates all other matrices
		for (int i = 1; i < number; i++) {
			// chooses randomly new different winner
			int newWinner = random.nextInt(K);
			while (newWinner == lastWinner)
				newWinner = random.nextInt(K);
			
			// generates new matrix and modifies it if possible
			double[][] newMatrix = generatePreferenceMatrix(K, newWinner, pmin);
			double[][] lastMatrix = matrices.get(matrices.size()-1);
			while (true) {
				if (deltaAtWinner) {
					int arm = random.nextInt(K);
					if (arm != lastWinner) {
						if (arm == newWinner) {
							if (newMatrix[newWinner][lastWinner] > pmin && lastMatrix[lastWinner][newWinner] + pmin - 1.0 <= delta) {
								newMatrix[lastWinner][newWinner] = lastMatrix[lastWinner][newWinner] - delta;
								newMatrix[newWinner][lastWinner] = 1.0 - newMatrix[lastWinner][newWinner];
								break;
							}
						} else {
							boolean up = false;
							if (lastMatrix[lastWinner][arm] + delta < 1)
								up = random.nextBoolean();
							newMatrix[lastWinner][arm] = lastMatrix[lastWinner][arm] + delta * (up ? 1.0 : -1.0);
							newMatrix[arm][lastWinner] = 1.0 - newMatrix[lastWinner][arm];
							break;
						}
					}
				} else {
					int arm1 = random.nextInt(K);
					int arm2 = random.nextInt(K);
					if (arm1 != arm2) {
						if ((arm1 == lastWinner && arm2 == newWinner) || (arm1 == newWinner && arm2 == lastWinner)) {
							if (newMatrix[newWinner][lastWinner] > pmin && lastMatrix[lastWinner][newWinner] + pmin - 1.0 <= delta) {
								newMatrix[lastWinner][newWinner] = lastMatrix[lastWinner][newWinner] - delta;
								newMatrix[newWinner][lastWinner] = 1.0 - newMatrix[lastWinner][newWinner];
								break;
							}
						} else if (arm1 == lastWinner || arm2 == lastWinner) {
							boolean up = random.nextBoolean();
							if (up) {
								if (lastMatrix[arm1][arm2] + delta <= 1) {
									newMatrix[arm1][arm2] = lastMatrix[arm1][arm2] + delta;
									newMatrix[arm2][arm1] = 1 - newMatrix[arm1][arm2];
									break;
								}
							} else {
								if (lastMatrix[arm1][arm2] - delta >= 0) {
									newMatrix[arm1][arm2] = lastMatrix[arm1][arm2] - delta;
									newMatrix[arm2][arm1] = 1 - newMatrix[arm1][arm2];
									break;
								}
							}
						} else if (arm1 == newWinner || arm2 == newWinner) {
							int arm = arm1 + arm2 - newWinner;
							if (newMatrix[newWinner][arm] > pmin && lastMatrix[newWinner][arm] + delta <= 1 && lastMatrix[newWinner][arm] + delta >= pmin) {
								newMatrix[newWinner][arm] = lastMatrix[newWinner][arm] + delta;
								newMatrix[arm][newWinner] = 1 - newMatrix[newWinner][arm];
								break;
							}
						} else {
							boolean up = random.nextBoolean();
							if (up) {
								if (lastMatrix[arm1][arm2] + delta <= 1) {
									newMatrix[arm1][arm2] = lastMatrix[arm1][arm2] + delta;
									newMatrix[arm2][arm1] = 1 - newMatrix[arm1][arm2];
									break;
								}
							} else {
								if (lastMatrix[arm1][arm2] - delta >= 0) {
									newMatrix[arm1][arm2] = lastMatrix[arm1][arm2] - delta;
									newMatrix[arm2][arm1] = 1 - newMatrix[arm1][arm2];
									break;
								}
							}
						}
					}
				}
			}
			matrices.add(newMatrix);
			lastWinner = newWinner;
		}
		
		return matrices;
	}
	
	public static double[][] generatePreferenceMatrix(int K, int condorcetWinner, double pmin) {
		double[][] matrix = new double[K][K];
		
		// every arm beats itself with probability 0.5
		for (int i = 0; i < K; i++)
			matrix[i][i] = 0.5;
		
		// random winning probabilities for all pairs
		for (int i = 0; i < K; i++) {
			for (int j = i+1; j < K; j++) {
				matrix[i][j] = Math.random();
				matrix[j][i] = 1.0 - matrix[i][j];
			}
		}
		
		// assign winning probabilities for the condorcet winner
		for (int j = 0; j < K; j++) {
			if (j != condorcetWinner) {
				matrix[condorcetWinner][j] = pmin + (1.0-pmin) * Math.random();
		        matrix[j][condorcetWinner] = 1.0 - matrix[condorcetWinner][j];
			}
		}
		
		// one winning probability of the condorcet winner is set to exactly pmin
		Random random = new Random();
		int selected = random.nextInt(K);
		while(selected == condorcetWinner)
			selected = random.nextInt(K);
		matrix[condorcetWinner][selected] = pmin;
        matrix[selected][condorcetWinner] = 1.0 - pmin;
		
		return matrix;
	}
	
	// returns the condorcet winner of a preference matrix
	public static int getCondorcetWinner(double[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			boolean beaten = false;
			// checks if an arm beats arm i with probability at least 0.5
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] < 0.5) {
					beaten = true;
					break;
				}
			}
			if (!beaten) 
				return i;
		}
		System.out.println("ERROR: no Condorcet Winner found!");
		return -1;
	}
	
	// returns the minimal maximum absolute change for the preference matrices
	public static double getDelta(List<double[][]> matrices) {
		if (matrices.size() < 2)
			return 0;
		
		double delta = getDelta(matrices.get(0), matrices.get(1)); 
		for (int i = 1; i < matrices.size()-1; i++)
			delta = Math.min(delta, getDelta(matrices.get(i), matrices.get(i+1)));
		
		return delta;
	}
	
	// returns the maximum absolute change between two preference matrices
	public static double getDelta(double[][] firstMatrix, double[][] secondMatrix) {
		double delta = 0;
		int K = firstMatrix.length;
		
		for (int i = 0; i < K-1; i++) {
			for (int j = i+1; j < K; j++)
				delta = Math.max(delta, Math.abs(firstMatrix[i][j] - secondMatrix[i][j]));
		}
		
		return delta;
	}
	
	// returns the minimal maximum absolute change for the condorcet winner for the preference matrices
	public static double getDeltaCondorcetWinner(List<double[][]> matrices) {
		if (matrices.size() < 2)
			return 0;
		
		double delta = getDeltaCondorcetWinner(matrices.get(0), matrices.get(1)); 
		for (int i = 1; i < matrices.size()-1; i++)
			delta = Math.min(delta, getDeltaCondorcetWinner(matrices.get(i), matrices.get(i+1)));
		
		return delta;
	}
	
	// returns the maximum absolute change for the condorcet winner between two preference matrices
	public static double getDeltaCondorcetWinner(double[][] firstMatrix, double[][] secondMatrix) {
		double delta = 0;
		int K = firstMatrix.length;
		int condorcetWinner = getCondorcetWinner(firstMatrix);
		
		for (int i = 0; i < K; i++)
			delta = Math.max(delta, Math.abs(firstMatrix[condorcetWinner][i] - secondMatrix[condorcetWinner][i]));
		
		return delta;
	}
	
	// generates random changepoints
	public static List<Integer> generateChangepoints(int number, int T, int minDistance) {
		List<Integer> changepoints = new ArrayList<Integer>();
		if (number == 0 || (number+1) * minDistance > T)
			return changepoints;
		
		Random random = new Random();
		int changepoint = random.nextInt(T-2*minDistance+1) + minDistance + 1;
		if (number == 1) {
			changepoints.add(changepoint);
			return changepoints;			
		}
			
		int leftCapacity = (changepoint-1) / minDistance - 1;
		int rightCapacity = (T-changepoint+1) / minDistance - 1;
		while (leftCapacity + rightCapacity < number - 1) {
			changepoint = random.nextInt(T-2*minDistance+1) + minDistance + 1;
			leftCapacity = (changepoint-1) / minDistance - 1;
			rightCapacity = (T-changepoint+1) / minDistance - 1;
		} 
		
		int leftNumber = 0;
		int rightNumber = 0;
		while (leftNumber < leftCapacity && rightNumber < rightCapacity && leftNumber + rightNumber < number - 1) {
			if (random.nextBoolean())
				leftNumber++;
			else
				rightNumber++;
		}
		
		if (leftNumber + rightNumber != number - 1) {
			if (leftNumber < leftCapacity)
				leftNumber = number - 1 - rightNumber;
			else
				rightNumber = number - 1 - leftNumber;
		}
		
		List<Integer> leftChangepoints = generateChangepoints(leftNumber, changepoint-1, minDistance);
		List<Integer> rightChangepoints = generateChangepoints(rightNumber, T-changepoint+1, minDistance);
		changepoints.addAll(leftChangepoints);
		changepoints.add(changepoint);
		for (int i = 0; i < rightChangepoints.size(); i++)
			changepoints.add(rightChangepoints.get(i) + changepoint - 1);
		
		return changepoints;
		
	}
	
	// writes data points of regret to file
	public static void writeToFile(String fileName, double[] regret, int[] records) {
		try {
			fileName += ".dat";
			File file = new File(fileName);
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
			
			writer.append("t" + "\t" + "regret" + "\n");
			writer.append(0 + "\t" + 0 + "\n");
			
			for (int i = 0; i < regret.length; i++)
				writer.append(records[i] + "\t" + regret[i] + "\n");
			
			writer.close();
		} catch (IOException e) {
			System.out.println("ERROR: writing to file failed!");
			e.printStackTrace();
		}
	}
	
	// writes a histogram to file
	public static void writeToFile(String fileName, Histogram hist) {
		try {
			fileName += ".dat";
			File file = new File(fileName);
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
			
			for (int i = 0; i < hist.getEntries().size(); i++) {
				for (int j = 0; j < hist.getEntries().get(i).getNumber(); j++)
					writer.append(hist.getEntries().get(i).getKey() + "\t" + 0 + "\n");
			}
			
			writer.close();
		} catch (IOException e) {
			System.out.println("ERROR: writing to file failed!");
			e.printStackTrace();
		}
	}
	
	// writes reset data to file
	public static void writeToFile(String fileName, double resets, double falseAlarms, double delay) {
		try {
			fileName += ".dat";
			File file = new File(fileName);
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
			
			writer.append("resets" + "\t" + resets + "\n");
			writer.append("falseAlarms" + "\t" + falseAlarms + "\n");
			writer.append("delay" + "\t" + delay + "\n");
			
			writer.close();
		} catch (IOException e) {
			System.out.println("ERROR: writing to file failed!");
			e.printStackTrace();
		}
	}
	
	// reads histogram from file
	public static Histogram readHistFromFile(String fileName) {
		Histogram hist = new Histogram();
		File file = new File(fileName); 
		try (Scanner sc = new Scanner(file)) {
			sc.nextLine();
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (!line.isBlank()) {
					String[] datapoint = line.split("\t");
					hist.addMany(Integer.valueOf(datapoint[0]), Integer.valueOf(datapoint[1]));
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: reading from file failed!");
			e.printStackTrace();
		}    
		return hist;
	}
	
	public static void printArray(int[] array) {
		for (int i = 0; i < array.length; i++)
			System.out.print(array[i] + " ");
		System.out.println();
	}
	
	public static void printArray(double[] array) {
		for (int i = 0; i < array.length; i++)
			System.out.print(array[i] + " ");
		System.out.println();
	}
	
	public static void printMatrix(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++)
				System.out.print(matrix[i][j] + " ");
			System.out.println();
		}
	}
	
	public static void printMatrix(double[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++)
				System.out.print(matrix[i][j] + " ");
			System.out.println();
		}
	}
	
}