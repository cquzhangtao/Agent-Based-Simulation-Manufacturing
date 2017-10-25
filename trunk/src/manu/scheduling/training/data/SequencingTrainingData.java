package manu.scheduling.training.data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import manu.agent.AgentBasedModel;
import manu.agent.component.Job;
import manu.model.component.BufferInfo;
import manu.model.component.JobInfo;
import manu.model.component.ToolGroupInfo;
import manu.model.component.ToolInfo;
import manu.scheduling.training.BPField;
import manu.scheduling.training.BPOutput;
import manu.scheduling.training.KmeansField;

public class SequencingTrainingData implements Cloneable, Serializable,
		GlobalData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	long decisionPointIndex;

	long currentTime;
	String toolGroupName;
	String jobType;

	// objective
	// @BPField
	// @BPOutput
	double weiAveCycTim;
	// @BPField
	// @BPOutput
	double[] weiAveCycTimD;
	// @BPField
	// @BPOutput
	double[] evaJobNumD;
	int evaluatedJobNum;

	@BPField
	@BPOutput
	double objective;

	@BPField
	@KmeansField
	double wip;
	double wipWeight = 0.12;
	// @BPField
	@KmeansField
	double[] wipD;
	double wipDWeight = 0.2;

	@BPField
	@KmeansField
	double bufferSize;
	double bufferSizeWeight = 0.02;

	@BPField
	@KmeansField
	double bufferTime;
	double bufferTimeWeight = 0.04;
	// @BPField
	@KmeansField
	double[] bufferSizeD;
	double bufferSizeDWeight = 0.1;
	// @BPField
	@KmeansField
	double[] bufferTimeD;
	double bufferTimeDWeight = 0.15;

	@KmeansField
	double unaTooRat;
	double unaTooRatWeight = 0;

	// @BPField
	@KmeansField
	double[] unaTooRatD;
	double unaTooRatDWeight = 0.12;

	@KmeansField
	double[] unaTooRatbyP;
	double unaTooRatbyPWeight = 0.0;
	// @BPField
	@KmeansField
	double[] totWaiTimD;
	double totWaiTimDWeight = 0.12;

	@KmeansField
	double[] totWaiTimbyP;
	double totWaiTimbyPWeight = 0.0;

	@BPField
	double iWipRat;
	double iWip;

	@BPField
	double procTandSetupTRat;
	double procTandSetupT;
	double processingTime;
	double setupTime;

	//@KmeansField
	double avgWaitingTime;
	
	@BPField
	double waitingTime;

	@BPField
	double remainTratio;
	double remainingTime;
	double rawProcessingTime;

	@BPField
	double remainSratio;
	double remainingStepNum;
	int totalStepNum;

	@BPField
	double jobNumBefRat;
	double jobNumBef;

	@BPField
	double jobNumAftRat;
	double jobNumAft;

	@BPField
	double jobNumHerRat;
	double jobNumHer;

	@BPField
	double nexBreNumRat;
	int nextToolBreakdownNum;
	int nextToolNum;

	@BPField
	double nexBufSiz;
	@BPField
	double nexBufTim;

	double batchingUrgency;

	public SequencingTrainingData(String name) {
		toolGroupName = name;
	}

	public SequencingTrainingData() {
		// TODO Auto-generated constructor stub
	}

	public void setLocalData(JobInfo job, BufferInfo buffer, ToolInfo tool,
			AgentBasedModel agentModel, long currentTime) {
		this.currentTime = currentTime;
		jobType = job.getProductName();
		bufferSize = buffer.size();
		bufferTime = buffer.getBufferTime();
		processingTime = job.getCurrentProcessTime();
		setupTime = tool.getSetupTime(job);
		procTandSetupT = processingTime + setupTime;
		procTandSetupTRat = procTandSetupT / (bufferTime + setupTime);
		waitingTime = currentTime - job.getStateStartTime();
		avgWaitingTime = 1.0 * buffer.getTotalWaitingTime(currentTime)
				/ bufferSize;
		remainingTime = job.getRemainTime().getAvg();
		rawProcessingTime = job.getTotalTime().getAvg();
		remainTratio = remainingTime / rawProcessingTime;
		remainingStepNum = job.getRemainStep();
		totalStepNum = job.getProcessNum();
		remainSratio = remainingStepNum / totalStepNum;
		iWip = agentModel.getReleaseAgent().getWipDetail()[job
				.getProductIndex()];
		iWipRat = 1.0 * iWip / wip;
		
		double[] nextData=getNextToolData(job, agentModel, currentTime);
		nexBreNumRat = nextData[0];
		nexBufSiz = nextData[1];
		nexBufTim = nextData[2];
		
		double[] jobData=getJobData(job,iWip,agentModel);
		jobNumAftRat = jobData[2];
		jobNumBefRat = jobData[0];
		jobNumHerRat = jobData[1];
		
		jobNumAft = jobData[5];
		jobNumBef = jobData[3];
		jobNumHer = jobData[4];

	}
	
	private double[] getJobData(JobInfo job,double wip,
			AgentBasedModel agentModel){
		double result[]=new double[6];
		double sum1 = 0,sum2=0,sum3=0;
		synchronized(agentModel.getJobAgents()){
		for(Job jobAgent:agentModel.getJobAgents()){
			if(job.getProductIndex()==jobAgent.getJobInfo().getProductIndex()){
			if(jobAgent.getJobInfo().getCurrentProcessIndex()<job.getCurrentProcessIndex()){
				sum1++;
			}else if(jobAgent.getJobInfo().getCurrentProcessIndex()>job.getCurrentProcessIndex()){
				sum3++;
			}else{
				sum2++;
			}
			}
		}
		}
		result[0]= sum1 / wip;
		result[1]= sum2 / wip;
		result[2]= sum3 / wip;
		result[3]= sum1;
		result[4]= sum2;
		result[5]= sum3;
		return result;
		
	}

	private double[] getNextToolData(JobInfo job,
			AgentBasedModel agentModel,long currentTime) {

		if (job.isLastProcess()) {
			return new double[]{0,0,0};
		}
		double result[]=new double[3];
		double sum1 = 0,sum2=0,sum3=0;
		List<ToolGroupInfo> nextToolGroups = agentModel.getReleaseAgent()
				.getProducts()[job.getProductIndex()].getProcessFlow()
				.get(job.getCurrentProcessIndex() + 1).getToolGroups();
		for (ToolGroupInfo tg : nextToolGroups) {
			ToolGroupInfo tgi = agentModel.getToolGroupAgent(tg.getToolGroupName()).getToolGroupInfo();
			sum1 += tgi.getUnavailableToolRatioD();
			sum2 += tgi.getBufferInfo().size();
			sum3 += tgi.getBufferInfo().getTotalWaitingTime(currentTime);
		}

		result[0]= sum1 / nextToolGroups.size();
		result[1]= sum2 / nextToolGroups.size();
		result[2]= sum3 / nextToolGroups.size();
		return result;

	}

	
	public SequencingTrainingData clone() {
		SequencingTrainingData data = new SequencingTrainingData();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				if (field.getName().equals("serialVersionUID"))
					continue;
				if (field.getType().isArray()) {
					double[] d = (double[]) field.get(this);
					if (d != null) {
						field.set(data, d.clone());
					}

				} else if (field.getType().equals(String.class)) {
					field.set(data, field.get(this));
				} else if (field.getType().equals(long.class)) {
					field.setLong(data, field.getLong(this));
				} else if (field.getType().equals(double.class)) {
					field.setDouble(data, field.getDouble(this));

				} else if (field.getType().equals(int.class)) {
					field.setInt(data, field.getInt(this));
				} else {
					System.out.println("Training data clone error");
					System.exit(1);
				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return data;

	}

	public long getDecisionPointIndex() {
		return decisionPointIndex;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public String getToolGroupName() {
		return toolGroupName;
	}

	public String getJobType() {
		return jobType;
	}

	public double getWeiAveCycTim() {
		return weiAveCycTim;
	}

	public double[] getWeiAveCycTimD() {
		return weiAveCycTimD;
	}

	public double[] getEvaJobNumD() {
		return evaJobNumD;
	}

	public int getEvaluatedJobNum() {
		return evaluatedJobNum;
	}

	public double getObjective() {
		return objective;
	}

	public double getWip() {
		return wip;
	}

	public double getWipWeight() {
		return wipWeight;
	}

	public double[] getWipD() {
		return wipD;
	}

	public double getWipDWeight() {
		return wipDWeight;
	}

	public double getBufferSize() {
		return bufferSize;
	}

	public double getBufferSizeWeight() {
		return bufferSizeWeight;
	}

	public double getBufferTime() {
		return bufferTime;
	}

	public double getBufferTimeWeight() {
		return bufferTimeWeight;
	}

	public double[] getBufferSizeD() {
		return bufferSizeD;
	}

	public double getBufferSizeDWeight() {
		return bufferSizeDWeight;
	}

	public double[] getBufferTimeD() {
		return bufferTimeD;
	}

	public double getBufferTimeDWeight() {
		return bufferTimeDWeight;
	}

	public double getUnaTooRat() {
		return unaTooRat;
	}

	public double getUnaTooRatWeight() {
		return unaTooRatWeight;
	}

	public double[] getUnaTooRatD() {
		return unaTooRatD;
	}

	public double getUnaTooRatDWeight() {
		return unaTooRatDWeight;
	}

	public double[] getUnaTooRatbyP() {
		return unaTooRatbyP;
	}

	public double getUnaTooRatbyPWeight() {
		return unaTooRatbyPWeight;
	}

	public double[] getTotWaiTimD() {
		return totWaiTimD;
	}

	public double getTotWaiTimDWeight() {
		return totWaiTimDWeight;
	}

	public double[] getTotWaiTimbyP() {
		return totWaiTimbyP;
	}

	public double getTotWaiTimbyPWeight() {
		return totWaiTimbyPWeight;
	}

	public double getiWipRat() {
		return iWipRat;
	}

	public double getiWip() {
		return iWip;
	}

	public double getProcTandSetupTRat() {
		return procTandSetupTRat;
	}

	public double getProcTandSetupT() {
		return procTandSetupT;
	}

	public double getProcessingTime() {
		return processingTime;
	}

	public double getSetupTime() {
		return setupTime;
	}

	public double getAvgWaitingTime() {
		return avgWaitingTime;
	}

	public double getWaitingTime() {
		return waitingTime;
	}

	public double getRemainTratio() {
		return remainTratio;
	}

	public double getRemainingTime() {
		return remainingTime;
	}

	public double getRawProcessingTime() {
		return rawProcessingTime;
	}

	public double getRemainSratio() {
		return remainSratio;
	}

	public double getRemainingStepNum() {
		return remainingStepNum;
	}

	public int getTotalStepNum() {
		return totalStepNum;
	}

	public double getJobNumBefRat() {
		return jobNumBefRat;
	}

	public double getJobNumBef() {
		return jobNumBef;
	}

	public double getJobNumAftRat() {
		return jobNumAftRat;
	}

	public double getJobNumAft() {
		return jobNumAft;
	}

	public double getJobNumHerRat() {
		return jobNumHerRat;
	}

	public double getJobNumHer() {
		return jobNumHer;
	}

	public double getNexBreNumRat() {
		return nexBreNumRat;
	}

	public int getNextToolBreakdownNum() {
		return nextToolBreakdownNum;
	}

	public int getNextToolNum() {
		return nextToolNum;
	}

	public double getNexBufSiz() {
		return nexBufSiz;
	}

	public double getNexBufTim() {
		return nexBufTim;
	}

	public double getBatchingUrgency() {
		return batchingUrgency;
	}

	public void setDecisionPointIndex(long decisionPointIndex) {
		this.decisionPointIndex = decisionPointIndex;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	public void setToolGroupName(String toolGroupName) {
		this.toolGroupName = toolGroupName;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public void setWeiAveCycTim(double weiAveCycTim) {
		this.weiAveCycTim = weiAveCycTim;
	}

	public void setWeiAveCycTimD(double[] weiAveCycTimD) {
		this.weiAveCycTimD = weiAveCycTimD;
	}

	public void setEvaJobNumD(double[] evaJobNumD) {
		this.evaJobNumD = evaJobNumD;
	}

	public void setEvaluatedJobNum(int evaluatedJobNum) {
		this.evaluatedJobNum = evaluatedJobNum;
	}

	public void setObjective(double objective) {
		this.objective = objective;
	}

	public void setWip(double wip) {
		this.wip = wip;
	}

	public void setWipWeight(double wipWeight) {
		this.wipWeight = wipWeight;
	}

	public void setWipD(double[] wipD) {
		this.wipD = wipD;
	}

	public void setWipDWeight(double wipDWeight) {
		this.wipDWeight = wipDWeight;
	}

	public void setBufferSize(double bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setBufferSizeWeight(double bufferSizeWeight) {
		this.bufferSizeWeight = bufferSizeWeight;
	}

	public void setBufferTime(double bufferTime) {
		this.bufferTime = bufferTime;
	}

	public void setBufferTimeWeight(double bufferTimeWeight) {
		this.bufferTimeWeight = bufferTimeWeight;
	}

	public void setBufferSizeD(double[] bufferSizeD) {
		this.bufferSizeD = bufferSizeD;
	}

	public void setBufferSizeDWeight(double bufferSizeDWeight) {
		this.bufferSizeDWeight = bufferSizeDWeight;
	}

	public void setBufferTimeD(double[] bufferTimeD) {
		this.bufferTimeD = bufferTimeD;
	}

	public void setBufferTimeDWeight(double bufferTimeDWeight) {
		this.bufferTimeDWeight = bufferTimeDWeight;
	}

	public void setUnaTooRat(double unaTooRat) {
		this.unaTooRat = unaTooRat;
	}

	public void setUnaTooRatWeight(double unaTooRatWeight) {
		this.unaTooRatWeight = unaTooRatWeight;
	}

	public void setUnaTooRatD(double[] unaTooRatD) {
		this.unaTooRatD = unaTooRatD;
	}

	public void setUnaTooRatDWeight(double unaTooRatDWeight) {
		this.unaTooRatDWeight = unaTooRatDWeight;
	}

	public void setUnaTooRatbyP(double[] unaTooRatbyP) {
		this.unaTooRatbyP = unaTooRatbyP;
	}

	public void setUnaTooRatbyPWeight(double unaTooRatbyPWeight) {
		this.unaTooRatbyPWeight = unaTooRatbyPWeight;
	}

	public void setTotWaiTimD(double[] totWaiTimD) {
		this.totWaiTimD = totWaiTimD;
	}

	public void setTotWaiTimDWeight(double totWaiTimDWeight) {
		this.totWaiTimDWeight = totWaiTimDWeight;
	}

	public void setTotWaiTimbyP(double[] totWaiTimbyP) {
		this.totWaiTimbyP = totWaiTimbyP;
	}

	public void setTotWaiTimbyPWeight(double totWaiTimbyPWeight) {
		this.totWaiTimbyPWeight = totWaiTimbyPWeight;
	}

	public void setiWipRat(double iWipRat) {
		this.iWipRat = iWipRat;
	}

	public void setiWip(double iWip) {
		this.iWip = iWip;
	}

	public void setProcTandSetupTRat(double procTandSetupTRat) {
		this.procTandSetupTRat = procTandSetupTRat;
	}

	public void setProcTandSetupT(double procTandSetupT) {
		this.procTandSetupT = procTandSetupT;
	}

	public void setProcessingTime(double processingTime) {
		this.processingTime = processingTime;
	}

	public void setSetupTime(double setupTime) {
		this.setupTime = setupTime;
	}

	public void setAvgWaitingTime(double avgWaitingTime) {
		this.avgWaitingTime = avgWaitingTime;
	}

	public void setWaitingTime(double waitingTime) {
		this.waitingTime = waitingTime;
	}

	public void setRemainTratio(double remainTratio) {
		this.remainTratio = remainTratio;
	}

	public void setRemainingTime(double remainingTime) {
		this.remainingTime = remainingTime;
	}

	public void setRawProcessingTime(double rawProcessingTime) {
		this.rawProcessingTime = rawProcessingTime;
	}

	public void setRemainSratio(double remainSratio) {
		this.remainSratio = remainSratio;
	}

	public void setRemainingStepNum(double remainingStepNum) {
		this.remainingStepNum = remainingStepNum;
	}

	public void setTotalStepNum(int totalStepNum) {
		this.totalStepNum = totalStepNum;
	}

	public void setJobNumBefRat(double jobNumBefRat) {
		this.jobNumBefRat = jobNumBefRat;
	}

	public void setJobNumBef(double jobNumBef) {
		this.jobNumBef = jobNumBef;
	}

	public void setJobNumAftRat(double jobNumAftRat) {
		this.jobNumAftRat = jobNumAftRat;
	}

	public void setJobNumAft(double jobNumAft) {
		this.jobNumAft = jobNumAft;
	}

	public void setJobNumHerRat(double jobNumHerRat) {
		this.jobNumHerRat = jobNumHerRat;
	}

	public void setJobNumHer(double jobNumHer) {
		this.jobNumHer = jobNumHer;
	}

	public void setNexBreNumRat(double nexBreNumRat) {
		this.nexBreNumRat = nexBreNumRat;
	}

	public void setNextToolBreakdownNum(int nextToolBreakdownNum) {
		this.nextToolBreakdownNum = nextToolBreakdownNum;
	}

	public void setNextToolNum(int nextToolNum) {
		this.nextToolNum = nextToolNum;
	}

	public void setNexBufSiz(double nexBufSiz) {
		this.nexBufSiz = nexBufSiz;
	}

	public void setNexBufTim(double nexBufTim) {
		this.nexBufTim = nexBufTim;
	}

	public void setBatchingUrgency(double batchingUrgency) {
		this.batchingUrgency = batchingUrgency;
	}

}
