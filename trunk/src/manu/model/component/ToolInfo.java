package manu.model.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import manu.model.component.state.ToolState;
import manu.model.feature.Interrupt;
import manu.simulation.result.data.ToolData;

public class ToolInfo implements Serializable {
	// static members
	public String toolName;
	public String toolType;
	public String toolGroupName;
	public boolean isBatchTool;
	public int toolIndex = 0;
	public int toolNuminGroup = 0;
	// public Setups setups;
	private Map<String, Long> setups;
	public String currentJobs = "";
	private ArrayList<Interrupt> interrupts = new ArrayList<Interrupt>();

	// dynamic members
	public int toolLife;
	public int state;
	public Interrupt waitInterrupt = null;
	public ToolData toolData;
	// for calculating setup time
	public String preJobType = "";
	public String currentJobType;
	public String preJobStepName = "";
	public String currentJobStepName = "";
	public int preBatchID;
	public int currentBatchID;
	// for stastics
	public long processedJobNum = 0;
	public int finishedJobNum = 0;
	public double processBeginTime;
	public double setupBeginTime = 0;
	public double freeBeginTime = 0;
	public double blockBeginTime = 0;
	public double breakdownBeginTime = 0;
	public double maintenanceBeginTime = 0;
	public double ratio;

	public int toolLife_s;
	public int state_s;
	public Interrupt waitInterrupt_s;
	public ToolData toolData_s;
	// for calculating setup time
	public String preJobType_s;
	public String currentJobType_s;
	public String preJobStep_s;
	public String currentJobStep_s;
	public int preBatchID_s;
	public int currentBatchID_s;
	// for stastics
	public long processedJobNum_s;
	public int finishedJobNum_s;
	public double processBeginTime_s;
	public double setupBeginTime_s;
	public double freeBeginTime_s;
	public double blockBeginTime_s;
	public double breakdownBeginTime_s;
	public double maintenanceBeginTime_s;
	public double ratio_s;
	private String currentJobs_s;

	public ToolInfo(String toolType, String toolName, String toolGroupName) {
		this.toolType = toolType;
		this.toolName = toolName;
		this.toolGroupName = toolGroupName;
		isBatchTool = false;
		state = ToolState.FREE;
		toolLife = 0;

		// toolTimeStat = new ToolData(ToolName, ToolGruopName);
	}

	public ToolInfo() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Interrupt> getInterrupt() {
		return getInterrupts();
	}

	public void setInterrupt(ArrayList<Interrupt> interrupt) {
		this.setInterrupts(interrupt);
	}

	public void addInterrupt(Interrupt inter) {
		Interrupt inter1 = inter.clone(toolIndex + 1982);
		inter1.index = getInterrupts().size();
		getInterrupts().add(inter1);
	}

	public void reset() {
		toolLife = 0;
		state = ToolState.FREE;
		preJobType = "";
		currentJobType = "";
		preJobStepName = "";
		currentJobStepName = "";

		// currentJobsName = null;
		// oldToolLife = 0;

		processBeginTime = 0;
		// allProcessTime = 0;
		processedJobNum = 0;
		ratio = 0;
		// totalSetupTime = 0;
		setupBeginTime = 0;
		freeBeginTime = 0;
		finishedJobNum = 0;
		waitInterrupt = null;
		blockBeginTime = 0;
		breakdownBeginTime = 0;
		maintenanceBeginTime = 0;
		// toolData .reset();
		currentJobs = "";
		for (Interrupt inter : getInterrupts()) {
			inter.reset();
		}

	}

	public void setToolData(ToolData data) {

		toolData = data;
		toolData.toolGroupName = toolGroupName;
		toolData.toolName = toolName;
	}

	public String GetCurrentStateString() {
		return ToolState.int2String(state);
	}

	public boolean IsFree() {
		return (state == ToolState.FREE);
	}

	public boolean isInterrupt() {
		return state == ToolState.BREAKDOWN || state == ToolState.MAINTENANCE;
	}

	public void onFinished() {
		preJobType = currentJobType;
		state = ToolState.FREE;
		toolLife++;
	}

	public boolean JobTypeChange() {
		if (preJobType.equals(""))
			return false;
		return !currentJobType.equals(preJobType);
	}

	public long getSetupTime() {
		// return 0;
		if (preJobStepName.isEmpty())
			return 0;
		return setups.get(preJobStepName + currentJobStepName);
	}

	public long getSetupTime(JobInfo jobInfo) {
		if (preJobStepName.isEmpty())
			return 0;
		return setups.get(preJobStepName + jobInfo.getCurrentProcessName());
	}

	// public double getSetupTime() {
	//
	// // return 0;
	// if (setups == null) {
	// //System.out.println("Setup data error!");
	// return 0;
	// }
	// Setup setup = setups.getSetup(currentJobType, currentJobStepName);
	// if (setup == null) {
	// // System.out.println("Setup data error!");
	// return 0;
	// }
	// if (setup.setupType.equals("sequence-dependent")) {
	// if (isBatchTool) {
	// return setup.getSetupTime(preBatchID, currentBatchID);
	// } else {
	// return setup.getSetupTime(preJobType, preJobStepName);
	// }
	// } else if (setup.setupType.equals("sequence-independent")) {
	// if (isBatchTool) {
	// return setup.getSetupTime(currentBatchID);
	// } else {
	// return setup.getSetupTime();
	// }
	// } else {
	// System.out.println("Setup data error!");
	// return 0;
	// }
	// }
	//
	// public double getSetupTime(JobinBuffer job) {
	//
	// // return 0;
	// if (setups == null) {
	// System.out.println("Setup data error!");
	// return 0;
	// }
	// Setup setup = setups.getSetup(job.jobType, job.curStepName);
	// if (setup == null) {
	// // System.out.println("Setup data error!");
	// return 0;
	// }
	// if (setup.setupType.equals("sequence-dependent")) {
	// if (isBatchTool) {
	// return setup.getSetupTime(preBatchID, currentBatchID);
	// } else {
	// return setup.getSetupTime(preJobType, preJobStepName);
	// }
	// } else if (setup.setupType.equals("sequence-independent")) {
	// if (isBatchTool) {
	// //return setup.getSetupTime(job.BatchID.);
	// return 0;
	// } else {
	// return setup.getSetupTime();
	// }
	// } else {
	// System.out.println("Setup data error!");
	// return 0;
	// }
	// }

	public void save() {
		toolLife_s = toolLife;
		state_s = state;
		if (waitInterrupt != null)
			waitInterrupt_s = waitInterrupt;
		// waitInterrupt.save();
		else
			waitInterrupt_s = null;
		// for calculating setup time
		preJobType_s = preJobType;
		currentJobType_s = currentJobType;
		preJobStep_s = preJobStepName;
		currentJobStep_s = currentJobStepName;
		preBatchID_s = preBatchID;
		currentBatchID_s = currentBatchID;
		// for stastics
		processedJobNum_s = processedJobNum;
		finishedJobNum_s = finishedJobNum;
		processBeginTime_s = processBeginTime;
		setupBeginTime_s = setupBeginTime;
		freeBeginTime_s = freeBeginTime;
		blockBeginTime_s = blockBeginTime;
		breakdownBeginTime_s = breakdownBeginTime;
		maintenanceBeginTime_s = maintenanceBeginTime;
		ratio_s = ratio;
		currentJobs_s = currentJobs;
		for (Interrupt inter : getInterrupts()) {
			inter.save();
		}
		// toolData.save();
	}

	public void recover() {

		toolLife = toolLife_s;
		state = state_s;

		waitInterrupt = waitInterrupt_s;

		preJobType = preJobType_s;
		currentJobType = currentJobType_s;
		preJobStepName = preJobStep_s;
		currentJobStepName = currentJobStep_s;
		preBatchID = preBatchID_s;
		currentBatchID = currentBatchID_s;
		processedJobNum = processedJobNum_s;
		finishedJobNum = finishedJobNum_s;
		processBeginTime = processBeginTime_s;
		setupBeginTime = setupBeginTime_s;
		freeBeginTime = freeBeginTime_s;
		blockBeginTime = blockBeginTime_s;
		breakdownBeginTime = breakdownBeginTime_s;
		maintenanceBeginTime = maintenanceBeginTime_s;
		ratio = ratio_s;
		currentJobs = currentJobs_s;
		for (Interrupt inter : getInterrupts()) {
			inter.recover();
		}

	}

	public ToolInfo clone() {
		ToolInfo ti = new ToolInfo();
		ti.toolName = toolName;
		ti.toolType = toolType;
		ti.toolGroupName = toolGroupName;
		ti.isBatchTool = isBatchTool;
		ti.toolIndex = toolIndex;
		ti.toolNuminGroup = toolNuminGroup;
		ti.setups = setups;
		ti.currentJobs = currentJobs;
		// private ArrayList<Interrupt> interrupts=new ArrayList<Interrupt> ();

		ti.toolLife = toolLife;
		ti.state = state;
		// public Interrupt waitInterrupt = null;

		// ti.toolData=toolData;

		ti.preJobType = preJobType;
		ti.currentJobType = currentJobType;
		ti.preJobStepName = preJobStepName;
		ti.currentJobStepName = currentJobStepName;

		// for stastics
		ti.processedJobNum = processedJobNum;
		ti.finishedJobNum = finishedJobNum;
		ti.processBeginTime = processBeginTime;
		ti.setupBeginTime = setupBeginTime;
		ti.freeBeginTime = freeBeginTime;
		ti.blockBeginTime = blockBeginTime;
		ti.breakdownBeginTime = breakdownBeginTime;
		ti.maintenanceBeginTime = maintenanceBeginTime;
		ti.ratio = ratio;

		return ti;
	}

	public void setInterrupts(ArrayList<Interrupt> interrupts) {
		this.interrupts = interrupts;
	}

	public ArrayList<Interrupt> getInterrupts() {
		return interrupts;
	}

	public Interrupt getInterruptByIndex(int index) {
		return interrupts.get(index);
	}

	public Map<String, Long> getSetups() {
		return setups;
	}

	public void setSetups(Map<String, Long> setups) {
		this.setups = setups;
	}

	public String getToolGroupName() {
		return toolGroupName;
	}

	public void setToolGroupName(String toolGroupName) {
		this.toolGroupName = toolGroupName;
	}

	public int getToolIndex() {
		return toolIndex;
	}

	public void setToolIndex(int toolIndex) {
		this.toolIndex = toolIndex;
	}
	
	public double getUtilization(){
		return toolData.getUtilization();
	}
}
