package manu.sbo.algorithm.antcolony;

public class DecisionVaraible {
	
	private double value;
	private double min;
	private double max;
	
	public DecisionVaraible(double max,double min)
	{
		this.max=max;
		this.min=min;
		this.value=0;
	}
	public void reset()
	{
		value=0;
	}
	public double getValue() {
		return value;
	}
	public double getMin() {
		return min;
	}
	public double getMax() {
		return max;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public void setMax(double max) {
		this.max = max;
	}
	
	public void setNormalizedValue(double value) {
		this.value = value*(max-min)+min;
	}

}
