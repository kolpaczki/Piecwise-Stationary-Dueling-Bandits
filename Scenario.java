import java.util.List;

public class Scenario {
	
	private final List<double[][]> preferenceMatrices;
	private final List<Integer> changepoints;
	
	public Scenario(List<double[][]> preferenceMatrices, List<Integer> changepoints) {
		this.preferenceMatrices = preferenceMatrices;
		this.changepoints = changepoints;
	}
	
	public List<double[][]> getPreferenceMatrices() {
		return preferenceMatrices;
	}
	
	public List<Integer> getChangepoints() {
		return changepoints;
	}
	
}