import java.util.ArrayList;
import java.util.List;

public abstract class NonstationaryDuelingAlgorithm extends DuelingAlgorithm {
	
	protected final Histogram hist;					// histogram for tracking resets by changepoint detection
	protected final List<Integer> resets;			// list of resets
	
	public NonstationaryDuelingAlgorithm(int K) {
		super(K);
		hist = new Histogram();
		resets = new ArrayList<Integer>();
	}
	
	public Histogram getHist() {
		return hist;
	}
	
	// returns the number of timesteps needed to fill a fixed window 
	public abstract int getWindowSpan();
	
	public abstract void printParameters();

}