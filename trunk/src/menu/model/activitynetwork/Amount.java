package menu.model.activitynetwork;

public class Amount {
	
	private double value;
	private Unit unit;
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	
	public double toUnit(Unit unit){
		//ToDo
		return value;
	}

}
