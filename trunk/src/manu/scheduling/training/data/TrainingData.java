package manu.scheduling.training.data;

import java.io.Serializable;

public interface TrainingData extends Cloneable, Serializable{
	
	public TrainingData getInstance();
	public double getFiledValue(String fieldName);
	public double[] getFiledValues(String fieldName);
}
