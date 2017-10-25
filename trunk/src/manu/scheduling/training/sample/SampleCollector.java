//package manu.scheduling.training.sample;
//
//import java.util.ArrayList;
//
//import manu.agent.AgentBasedModel;
//import manu.agent.ToolGroup;
//import manu.model.ManufactureModel;
//import manu.model.component.BufferInfo;
//import manu.model.component.ToolGroupInfo;
//import manu.model.component.ToolInfo;
//import manu.model.componentnew.JobInfo;
//import manu.scheduling.training.data.TrainingData;
//import manu.scheduling.training.data.TrainingDataset;
//import manu.scheduling.training.data.TrainingDatasetMap;
//import manu.simulation.Simulation;
//import manu.simulation.result.SimulationResult;
//
//public class SampleCollector {
//	final ManufactureModel manuModel;
//	Simulation simulator;
//	AgentBasedModel agentModel;
//	SimulationResult results;
//
//	public SampleToolGroup currentSampleTG = new SampleToolGroup();
//	public OptionalJobs optionalJobs = new OptionalJobs();
//	public ArrayList<String> optionalJobsTemp;
//
//	private RecoveredFlag recoveredFlag = new RecoveredFlag();
//	private RecoverableFlag recoverableFlag = new RecoverableFlag();
//	private ContinueSimFlag continueFlag = new ContinueSimFlag();
//	private ArrayList<Double>[] cycleTime;
//	public double[] performances;
//
//	ToolInfo currentTool;
//	BufferInfo currentBuffer;
//	long currentTime;
//	int moreJobNum = 10;
//
//	long decisionPointIndex = 0;
//
//	TrainingDatasetMap traningDatasetMap = new TrainingDatasetMap();
//	
//	long saveandRecoverTime=0;
//
//	public TrainingDatasetMap getTraningDatasetMap() {
//		return traningDatasetMap;
//	}
//
//	public void setTraningDatasetMap(TrainingDatasetMap traningDatasetMap) {
//		this.traningDatasetMap = traningDatasetMap;
//	}
//
//	TrainingData[] currentTrainingData;
//
//	JobInfo selectedJob;
//	public boolean sample = false;
//
//	public SampleCollector(Simulation simulator, ManufactureModel manuModel,
//			AgentBasedModel agentModel, SimulationResult results) {
//		this.agentModel = agentModel;
//		this.results = results;
//		this.simulator = simulator;
//		this.manuModel = manuModel;
//		//simulator.collector = this;
//		//20140407
//		//sample = simulator.simulatorInfo.sampleTrainingData;
//		//
//
//	}
//
//	public void initSampleDataset() {
//		for (ToolGroupInfo tg : manuModel.toolGroups) {
//			TrainingDataset dataset = new TrainingDataset();
//			dataset.setToolGroupName(tg.toolGroupName);
//			traningDatasetMap.putDataset(tg.toolGroupName, dataset);
//		}
//	}
//
//	public void onJobFinish(String jobName, int jobTypeIndex, long cycleT) {
//		if (!sample)
//			return;
//		if (!currentSampleTG.isEmpty()) {
//			optionalJobsTemp.remove(jobName);
//			addCycleTime(jobTypeIndex, cycleT);
//			if (optionalJobsTemp.size() < 1) {
//				moreJobNum--;
//			}
//			if (moreJobNum == 0) {
//				double sum = 0;
//				int vJobNum = 0;
//				double[] jobNum = new double[cycleTime.length];
//				double[] jobCycleTime = new double[cycleTime.length];
//				for (int i = 0; i < cycleTime.length; i++) {
//					double sum1 = 0;
//					for (int j = 0; j < cycleTime[i].size(); j++) {
//						sum1 += cycleTime[i].get(j).doubleValue();
//					}
//					jobNum[i] = cycleTime[i].size();
//
//					if (sum1 != 0 && cycleTime[i].size() > 0) {
//						sum += sum1 / cycleTime[i].size()
//								* manuModel.products[i].getReleaseProb();
//						jobCycleTime[i] = sum1 / cycleTime[i].size();
//					} else {
//						jobCycleTime[i] = 0;
//					}
//					vJobNum += cycleTime[i].size();
//				}
//				double weightSum = 0;
//				for (int i = 0; i < manuModel.products.length; i++)
//					weightSum += manuModel.products[i].getReleaseProb();
//				double avg = sum / weightSum;
//				performances[optionalJobs.index - 1] = avg;
//				currentTrainingData[optionalJobs.index - 1]
//						.setWeightedAveCT(avg);
//				currentTrainingData[optionalJobs.index - 1]
//						.setEvaluatedJobNum(vJobNum);
//				currentTrainingData[optionalJobs.index - 1]
//						.setWeightedAveCTD(jobCycleTime);
//				currentTrainingData[optionalJobs.index - 1]
//						.setEvaluatedJobNumD(jobNum);
//				clearCycleTimeArray();
//
//				if (optionalJobs.isFinished()) {
//					double min = Double.MAX_VALUE;
//					double max = Double.MIN_VALUE;
//					int minIndex = 0;
//					for (int i = 0; i < performances.length; i++) {
//						if (min > performances[i]) {
//							minIndex = i;
//							min = performances[i];
//						}
//						if (max < performances[i]) {
//							max = performances[i];
//						}
//					}
//					selectedJob = optionalJobs.list.get(minIndex);
//					selectedJob=optionalJobs.list.get(0);
//					// String str="";
//					// for(double d:performances){
//					// str+=","+(int)d;
//					// }
//					// System.out.println("Performance: "+str+", difference: "+(int)(max-min));
//					setContinue(true);
//					setRecoverable(true);
//				} else {
//					setRecoverable(true);
//				}
//			}
//		}
//		//
//		else {
//
//		}
//	}
//
//	public ArrayList<JobInfo> onDecisionPoint(BufferInfo bufferInfo, ToolInfo toolInfo,
//			long currentTime,ToolGroup toolgroup) {
//		if (!sample||bufferInfo.size()<2)
//			return null;
//		//String[] job = null;
//		ArrayList<JobInfo>jobs=new ArrayList<JobInfo>();
//		if (currentSampleTG.isEmpty() && !bufferInfo.isBatchTool()
//				&& bufferInfo.size() > 1) {
//			decisionPointIndex++;
//			optionalJobs.set(bufferInfo.getJobsinBufferClone());
//			optionalJobsTemp = optionalJobs.cloneString();
//			currentTool = toolInfo;
//			currentBuffer = bufferInfo;
//			this.currentTime = currentTime;
//			moreJobNum = 10;
//			currentTrainingData = new TrainingData[bufferInfo.size()];
//			for (int i = 0; i < bufferInfo.size(); i++) {
//				currentTrainingData[i] = new TrainingData(
//						currentTool.toolGroupName);
//				currentTrainingData[i]
//						.setDecisionPointIndex(decisionPointIndex);
//			}
//			setRecovered(true);
//			clearCycleTimeArray();
//			selectedJob = null;
//			performances = new double[optionalJobs.list.size()];
//
//			currentSampleTG.set(toolInfo.toolGroupName);// put at the end
//			// System.out.println("\n********************************************************************************\n"
//			// +"*******************************Interruption starts******************************\n"
//			// +"********************************************************************************\n"+
//			// name+", optional jobs "+optionalJobs.toString()+","+simulator.currentTime());
//		}
//		if (currentSampleTG.isMe(toolInfo.toolGroupName) && isRecovered()
//				&& !isRecoverable()) {
//			JobInfo job;
//			if (isContinue()) {
//				currentSampleTG.reset();
//				setContinue(false);
//				job = selectedJob;	
//				
//				job=toolgroup.getBestJob(toolInfo).get(0);
//				// System.out.println(name+", selected job "+j.jobName+","+simulator.currentTime()+"\n"+
//				// "********************************************************************************\n"
//				// +"*******************************Interruption ends******************************\n"
//				// +"********************************************************************************");
//			} else {
//				job = optionalJobs.getJob();
//				// System.out.println(name+", current job tested "+j.jobName+","+simulator.currentTime());
//			}
//			setRecovered(false);
//			bufferInfo.getWaitDataset().add(job.getName(), currentTime, currentTime
//					- job.getStateStartTime());
//			bufferInfo.remove(job);
//			jobs.add(job);
//			return jobs;
//		}
//		//System.out.println("sample collector error on decison making point");
//		return null;
//		
//	}
//
//	public void beforeActivation() {
//		if (!sample)
//			return;
//		if (currentSampleTG.isEmpty() && !isRecoverable()) {
//			save();
//			// System.out.println("Data is saved, jobs "+jobstoString());
//
//		}
//		if (isRecoverable()) {
//			optionalJobsTemp = optionalJobs.cloneString();
//			moreJobNum = 10;
//			// optionalJobsTemp=agentModel.getCurrentJobs();
//			recover();
//			// System.out.println("*******************************Recover******************************************");
//			// System.out.println("Data is recovered, jobs "+jobstoString());
//			if (optionalJobs.index == 1)
//				addTrainingData();
//			setRecovered(true);
//			setRecoverable(false);
//
//		}
//	}
//
//	public void addTrainingData() {
//		int wip=agentModel.getCurrentWip();
//		int[]wipD=agentModel.getCurrentWipD();
//		double unavailableToolRatio=manuModel
//		.getUnavailableToolRatio();
//		double[]unavailableToolRatiobyP=manuModel
//		.getUnavailableToolRatiobyP();
//		double[]unavailableToolRatioD=manuModel.getUnavailableToolRatioD();
//		double[]bufferSizeD=agentModel.getBufferSizeD(currentTool.toolGroupName);
//		double[]bufferTimeD=agentModel.getBufferTimeD(currentTool.toolGroupName);
//		double[]totWaitingTimeD=agentModel.getTotalWaitingTimeD(currentTime);
//		double[]totWaitingTimebyP=agentModel.getTotalWaitingTimebyP(currentTime);
//		
//		int index = 0;
//		for (JobInfo job : optionalJobs.list) {
//			//currentTrainingData[index].setWipInfo(wip, agentModel.getCurrentWip(job.getJobTypeIndex()), wipD.clone());
////			currentTrainingData[index].setData(job, currentBuffer, currentTool, currentTime);
////
////			if (!job.nextToolGroupName.equals("Last")) {
////				BufferInfo nextbuffer = agentModel
////						.getToolGroup(job.nextToolGroupName).bufferInfo;
////				currentTrainingData[index].setNextBufferTime(nextbuffer
////						.getBufferTime());
////				currentTrainingData[index].setNextBufferSize(nextbuffer.size());
////				currentTrainingData[index].setNextToolBreakdown(manuModel
////						.getToolState(job.nextToolGroupName));
////				if (nextbuffer.isBatchTool)
////					currentTrainingData[index].setBatchingUrgency(nextbuffer
////							.batchingUrgency(job.nextBatchID));
////			}
////			currentTrainingData[index].setUnavailableToolRatio(unavailableToolRatio);
////			currentTrainingData[index].setUnavailableToolRatiobyP(unavailableToolRatiobyP.clone());
////			currentTrainingData[index].setUnaTooRatD(unavailableToolRatioD.clone());
////			currentTrainingData[index].setBuffersizeD(bufferSizeD.clone());
////			currentTrainingData[index].setBufferTimeD(bufferTimeD.clone());
////			currentTrainingData[index].setJobNum(agentModel.getJobNum(
////					job.jobTypeIndex, job.curStepIndex));
////			currentTrainingData[index].setTotWaiTimbyP(totWaitingTimebyP.clone());
////			currentTrainingData[index].setTotWaiTimD(totWaitingTimeD.clone());
////			traningDatasetMap.getDataset(currentTool.toolGroupName).add(
////					currentTrainingData[index]);
//			index++;
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public void createCycleTimeArray(int productNum) {
//		cycleTime = new ArrayList[productNum];
//		for (int i = 0; i < productNum; i++) {
//			cycleTime[i] = new ArrayList<Double>();
//		}
//	}
//
//	public void clearCycleTimeArray() {
//		for (int i = 0; i < agentModel.getReleaseAgent().getProducts().length; i++) {
//			cycleTime[i].clear();
//		}
//	}
//
//	public void addCycleTime(int typeIndex, long cycleT) {
//		cycleTime[typeIndex].add(cycleT / 60.0);
//	}
//
//	public void save() {
//		
//		long begin=System.currentTimeMillis();
//		//simulator.save();
//		//results.save();
//		//agentModel.save();
//		saveandRecoverTime+=System.currentTimeMillis()-begin;
//	}
//
//	public void recover() {
//		long begin=System.currentTimeMillis();
//		//agentModel.recover();
//		//results.recover();
//		//simulator.recover(agentModel.environment);
//		saveandRecoverTime+=System.currentTimeMillis()-begin;
//	}
//
//	public boolean isRecoverable() {
//		return recoverableFlag.isRecoverable();
//	}
//
//	public void setRecovered(boolean b) {
//		recoveredFlag.setRecovered(b);
//	}
//
//	public void setRecoverable(boolean b) {
//		recoverableFlag.setRecoverable(b);
//	}
//
//	public boolean isRecovered() {
//		return recoveredFlag.isRecovered();
//	}
//
//	public boolean isContinue() {
//		return continueFlag.continueSim;
//	}
//
//	public void setContinue(boolean b) {
//		continueFlag.setContinue(b);
//	}
//
//	public void saveToXls() {
//		if (!sample)
//			return;
//		System.out.println("time "+saveandRecoverTime/1000.0);
//		try {
//			traningDatasetMap.saveToXls();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}
