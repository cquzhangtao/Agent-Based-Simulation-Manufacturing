package manu.utli;

public class StatValue {
	private long min;
	private long max;
	private long avg;
	
	
	public StatValue(){
		min=Long.MAX_VALUE;
		max=Long.MIN_VALUE;
	}
	public long getMin() {
		return min;
	}
	public long getMax() {
		return max;
	}

	public void setMin(long min) {
		this.min = min;
	}
	public void setMax(long max) {
		this.max = max;
	}
	public long getAvg() {
		return avg;
	}
	public void setAvg(long avg) {
		this.avg = avg;
	}

	

}
