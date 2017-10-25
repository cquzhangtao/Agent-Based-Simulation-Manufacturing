package manu.simulation.info;


import java.io.Serializable;

import manu.simulation.SimEndCondition;


public class SimulationInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8557170970677926877L;
	public int guiShowOrNot = 0;
	public final boolean logging = false;
	public long simulateBeginTime;// real time
	public String informationAgentName = "Info";
	public String releaseAgentName = "Release";
	public int planLotNum;
	public double planTime = 100;// days
	// 0-end by plan lot number,1-end by plan time
	public int simulateEndCondition = SimEndCondition.EndByPlanTime;
	public int simulationTimes=1;
	public long test = 10000000;
	public int dataAcqTime;
	public String dataAcqTimeStr="week";
	public String currentModelName = "";
	public boolean bufferDataOutput = true;
	public boolean waitTimeOutput = true;
	public boolean blockTimeOutput = true;
	public boolean toolDataOutput = true;
	public boolean ReleaseDataOutput = true;
	public boolean jobDataOutput = true;
	public String currentModelPath;
	public long runTime;
	public long simulationTime;
	//public int avgProcessStepNum;
	public boolean sampleTrainingData=false;





	public SimulationInfo() {

	
	}

	public void setValue(SimulationInfo info) {
		guiShowOrNot = info.guiShowOrNot;
		// logged=info.logged;
		// simulateBeginTime=info.simulateBeginTime;
		planTime = info.planTime;
		simulateEndCondition = info.simulateEndCondition;
		//releasePolicy = info.releasePolicy;
		//costWIP = info.costWIP;
		//intervalTime = info.intervalTime;
		// simulateIndex=info.simulateIndex;
		// activeAgentName=info.activeAgentName;
		// activeEventName=info.activeEventName;
		// currentTime=info.currentTime;
		//jobType = info.jobType;
		dataAcqTime = info.dataAcqTime;
		currentModelName = info.currentModelName;
		bufferDataOutput = info.bufferDataOutput;
		waitTimeOutput = info.waitTimeOutput;
		blockTimeOutput = info.blockTimeOutput;
		toolDataOutput = info.toolDataOutput;
		ReleaseDataOutput = info.ReleaseDataOutput;
		jobDataOutput = info.jobDataOutput;
		planLotNum = info.planLotNum;

	}

	public void setPlanTime(double planTime) {
		this.planTime = planTime;
	}

	public int getDataAcqTime() {
		return dataAcqTime;
	}

	public void setDataAcqTime(int dataAcqTime) {
		this.dataAcqTime = dataAcqTime;
	}



}
