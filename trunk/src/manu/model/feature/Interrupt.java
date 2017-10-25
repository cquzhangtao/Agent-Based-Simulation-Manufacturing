package manu.model.feature;

import manu.simulation.others.CommonMethods;
import simulation.distribution.RollbackRandom;

public class Interrupt {
	public int index;
	public String interruptType;
	public String distribution;
	public String applyTo;
	public double[] para = new double[3];
	public String recoveryDistribution;
	public double[] recoveryPara = new double[3];
	public int specifiedToolIndex=0;
	private String range="Tool";
	
	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public RollbackRandom rand=new RollbackRandom();
	public long nextOccurAfterTime() {
		return (long) CommonMethods.getRandom(distribution, para,rand) * 60 * 60;
	}

	public long recoveryTime() {
		return (long) CommonMethods.getRandom(recoveryDistribution,
				recoveryPara,rand) * 60;
	}
	
	public void save(){
		rand.save();
	}
	public void recover(){
		rand.recover();
	}
	public void reset(){
		rand.reset();
	}
	
	public Interrupt clone(int seed){
		Interrupt intr=new Interrupt();
		intr.index=index;
		intr.interruptType=interruptType;
		intr.distribution=distribution;
		intr.applyTo=applyTo;
		intr.para=para.clone();
		intr.recoveryDistribution=this.recoveryDistribution;
		intr.recoveryPara=this.recoveryPara.clone();
		intr.rand.setSeed(seed);
		return intr;
	}
}
