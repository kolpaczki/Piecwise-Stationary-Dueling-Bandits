import java.util.ArrayList;
import java.util.List;

public class Histogram {
	
	private final List<HistogramEntry> entries = new ArrayList<HistogramEntry>();	// list of entries ordered by their keys
	
	public void add(int key) {
		// searches for the key or position in between to insert a new entry to the list 
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).getKey() == key) {
				entries.get(i).add();
				return;
			} else if (key < entries.get(i).getKey()) {
				entries.add(i, new HistogramEntry(key));
				return;
			}
		}
		entries.add(new HistogramEntry(key));
	}
	
	// adds multiple entries with the same key
	public void addMany(int key, int number) {
		for (int i = 0; i < number; i++)
			add(key);
	}
	
	public List<HistogramEntry> getEntries() {
		return entries;
	}
	
	// returns the total number of multiple entries (even with same key)
	public int getTotalNumber() {
		int sum = 0;
		for (int i = 0; i < entries.size(); i++)
			sum += entries.get(i).getNumber();
		return sum;
	}
	
	// returns the total number of false alarms
	public int getTotalFalseAlarms(List<Integer> changepoints, int w) {
		int sum = 0;
		for (int i = 0; i < entries.size(); i++) {
			boolean isFalseAlarm = true;
			for (int j = 0; j < changepoints.size(); j++) {
				if (entries.get(i).getKey() - w + 1 < changepoints.get(j) && changepoints.get(j) <= entries.get(i).getKey())
					isFalseAlarm = false;
			}
			if (isFalseAlarm)
				sum += entries.get(i).getNumber();
		}
		return sum;
	}
	
	public double getAverageDelay(List<Integer> changepoints, int w) {
		Average avg = new Average();
		for (int i = 0; i < entries.size(); i++) {
			// checks first whether it is a false alarm
			boolean isFalseAlarm = true;
			for (int j = 0; j < changepoints.size(); j++) {
				if (entries.get(i).getKey() - w + 1 < changepoints.get(j) && changepoints.get(j) <= entries.get(i).getKey())
					isFalseAlarm = false;
			}
			// updates the average delay if not
			if (!isFalseAlarm) {
				for (int j = 0; j < changepoints.size()-1; j++) {
					if (changepoints.get(j) <= entries.get(i).getKey() && entries.get(i).getKey() < changepoints.get(j+1))
						avg.add(entries.get(i).getNumber() * (entries.get(i).getKey() - changepoints.get(j)));
				}
				if (changepoints.get(changepoints.size()-1) <= entries.get(i).getKey())
					avg.add(entries.get(i).getNumber() * (entries.get(i).getKey() - changepoints.get(changepoints.size()-1)));
			}
		}
		return avg.getAverage();
	}
	
	// returns a Histogram with all reset times calibrated to their most recent changepoint
	public Histogram getCalibrated(List<Integer> changepoints) {
		if (changepoints.isEmpty()) {
			return this;
		}
		
		Histogram hist = new Histogram(); 
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).getKey() < changepoints.get(0))
				hist.addMany(entries.get(i).getKey(), entries.get(i).getNumber());
			else {
				for (int j = changepoints.size()-1; j >= 0; j--) {
					if (entries.get(i).getKey() >= changepoints.get(j)) {
						hist.addMany(entries.get(i).getKey() - changepoints.get(j)+1, entries.get(i).getNumber());
						break;
					}
				}
			}
		}
		
		return hist;
	}
	
	public void print() {
		for (int i = 0; i < entries.size(); i++)
			System.out.println(entries.get(i).getKey() + "\t" + entries.get(i).getNumber());
	}
	
}