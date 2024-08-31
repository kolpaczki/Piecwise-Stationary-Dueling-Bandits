public abstract class Window {
	
	protected final int length;		// window length
	
	public Window(int length) {
		this.length = length;
	}
	
	// adds the bit to the window
	public abstract void push(Boolean element);
	
	// clears the window
	public abstract void clear();
	
	// returns true if the window is filled
	public abstract boolean isFilled();
	
	// returns the sum of the recent half of bits 
	public abstract int getFirstSum();
	
	// returns the sum of the older half of bits
	public abstract int getSecondSum();
	
}