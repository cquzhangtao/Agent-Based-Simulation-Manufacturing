package manu.scheduling.training;

import java.io.Serializable;

public class ClusterInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int classNum=5;
	private int epoch=10;
	private double tolerance=0.000001;
	private int maxIterate=50;
	
	public ClusterInfo clone(){
		ClusterInfo info=new ClusterInfo();
		info.classNum=classNum;
		info.epoch=epoch;
		info.tolerance=tolerance;
		info.maxIterate=maxIterate;
		return info;
		
	}
	
	public int getEpoch() {
		return epoch;
	}

	public void setEpoch(int epoch) {
		this.epoch = epoch;
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}


	public int getClassNum() {
		return classNum;
	}

	public void setClassNum(int classNum) {
		this.classNum = classNum;
	}

	public int getMaxIterate() {
		return maxIterate;
	}

	public void setMaxIterate(int maxIterate) {
		this.maxIterate = maxIterate;
	}
	

}
