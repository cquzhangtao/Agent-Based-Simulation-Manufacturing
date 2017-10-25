package manu.scheduling.training.data;

import java.io.Serializable;

public class ActionAndValue<T> implements Serializable{
	private T action;
	private double value;
	
	public ActionAndValue(T action, double value){
		this.action=action;
		this.value=value;
	}
	
	public T getAction() {
		return action;
	}
	public void setAction(T action) {
		this.action = action;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	

}
