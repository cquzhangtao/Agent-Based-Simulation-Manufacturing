package manu.scheduling.training;

import java.io.Serializable;

public class BPInfo implements Serializable{
	private double eta;// learning rate
	private double momentum;
	private int epoch;
	private int hiddenNodeNum;
	private double tolerance;
	private double minGradient;
	private int maxFailNum;
	
	public BPInfo clone(){
		BPInfo info=new BPInfo();
		info.eta=eta;
		info.maxFailNum=maxFailNum;
		info.minGradient=this.minGradient;
		info.momentum=this.momentum;
		info.hiddenNodeNum=this.hiddenNodeNum;
		info.tolerance=this.tolerance;
		info.epoch=this.epoch;
		return info;
		
	}
	public int getMaxFailNum() {
		return maxFailNum;
	}
	public void setMaxFailNum(int maxFailNum) {
		this.maxFailNum = maxFailNum;
	}
	public double getMinGradient() {
		return minGradient;
	}
	public void setMinGradient(double minGradient) {
		this.minGradient = minGradient;
	}
	public double getEta() {
		return eta;
	}
	public void setEta(double eta) {
		this.eta = eta;
	}
	public double getMomentum() {
		return momentum;
	}
	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}
	public int getEpoch() {
		return epoch;
	}
	public void setEpoch(int epoch) {
		this.epoch = epoch;
	}
	public int getHiddenNodeNum() {
		return hiddenNodeNum;
	}
	public void setHiddenNodeNum(int hiddenNodeNum) {
		this.hiddenNodeNum = hiddenNodeNum;
	}
	public double getTolerance() {
		return tolerance;
	}
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}
	
	

}
