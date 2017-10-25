package manu.simulation.info;


import java.io.Serializable;

import manu.model.component.ReleasePolicy;
import manu.simulation.SimEndCondition;


public class SimulatorInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int guiShowOrNot = 0;
	public final boolean logging = false;
	public long simulateBeginTime;// real time
	public String informationAgentName = "Info";
	public String releaseAgentName = "Release";
	public int planLotNum;
	public long planTime = 100;// days
	// 0-end by plan lot number,1-end by plan time
	public int simulateEndCondition = SimEndCondition.EndByPlanTime;
	public int releasePolicy = ReleasePolicy.ConstantTime;
	//public int beginningReleasePolicy;
	public int costWIP = 15;
	public int intervalTime = 120;// minutes
	public long test = 10000000;
	public int dataAcqTime;
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
	public int avgProcessStepNum;





	public SimulatorInfo() {

	
	}

	public void setValue(SimulatorInfo info) {
		guiShowOrNot = info.guiShowOrNot;
		// logged=info.logged;
		// simulateBeginTime=info.simulateBeginTime;
		planTime = info.planTime;
		simulateEndCondition = info.simulateEndCondition;
		releasePolicy = info.releasePolicy;
		costWIP = info.costWIP;
		intervalTime = info.intervalTime;
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



}
