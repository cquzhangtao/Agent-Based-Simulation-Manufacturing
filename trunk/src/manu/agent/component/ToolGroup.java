package manu.agent.component;

import java.util.ArrayList;
import java.util.Random;

import manu.agent.AgentBasedModel;
import manu.model.component.BufferInfo;
import manu.model.component.JobInfo;
import manu.model.component.ToolGroupInfo;
import manu.model.component.ToolInfo;
import manu.model.component.state.ToolState;
import manu.model.feature.DispatchingRules;
import manu.model.feature.Interrupt;
import manu.scheduling.DecisionMaker;
import manu.scheduling.SubSimulation;
import manu.scheduling.training.BPField;
import manu.scheduling.training.BPNetwork;
import manu.scheduling.training.Kmeans;
import manu.scheduling.training.KmeansField;
import manu.scheduling.training.Normalizer;
import manu.scheduling.training.Training;
import manu.scheduling.training.data.DataMeta;
import manu.scheduling.training.data.GlobalDataCompute;
import manu.scheduling.training.data.SequencingTrainingData;
import manu.scheduling.training.data.TrainingDataset;
import manu.scheduling.training.data.TrainingDatasetRaw;
import manu.simulation.CustomActivation;
import manu.simulation.result.data.BlockDataset;
import manu.simulation.result.data.BufferData;
import manu.simulation.result.data.BufferDataset;
import manu.simulation.result.data.ToolGroupData;
import manu.simulation.result.data.ToolGroupDataset;
import simulation.distribution.RollbackRandom;
import simulation.framework.Activation;
import simulation.framework.AgentEnvironment;
import simulation.framework.Behaviour;
import simulation.framework.Message;

public class ToolGroup extends Buffer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ToolGroupInfo toolGroupInfo;
	private BufferInfo bufferInfo;
	private BufferData bufferData;
	private BlockDataset blockDataset;

	// private DecisionMaker<SequencingTrainingData> decisionMaker;
	private String release;

	private RollbackRandom random = new RollbackRandom();

	private TransportRequestServer transportRequestServer;
	private WaitRequestServer waitRequestServer;
	private JobFinishServer jobFinishServer;
	private JobBlockServer jobBlockServer;
	private ToolBreakdownServer toolBreakdownServer;
	private ToolRepairedServer toolRepairedServer;
	private SetupEndServer setupEndServer;

	public ToolGroup(String agentName, Object[] args) {
		super(agentName);
		this.setAdditionalString("Main Simulation");
		if (args != null && args.length > 0) {
			toolGroupInfo = (ToolGroupInfo) args[0];
			bufferData = ((BufferData) (args[1]));
			ToolGroupData toolGroupData = (ToolGroupData) args[2];
			setRelease((String) args[3]);
			setSubSimulation((SubSimulation) args[4]);
			// decisionMaker = toolGroupInfo.getDisicionMaker();
			// if (decisionMaker != null)
			// decisionMaker
			// .setDataset(new TrainingDataset<SequencingTrainingData>(
			// getAgentName()));
			bufferInfo = new BufferInfo(toolGroupInfo.getRule(), toolGroupInfo.getBufferCapacity(), toolGroupInfo.isBatchTool(), toolGroupData.waitDataset);
			toolGroupInfo.setBufferInfo(getBufferInfo());
			blockDataset = toolGroupData.blockDataset;

		} else {
			System.out.println("Failure Load Tool");
			return;
		}
		setupEndServer = new SetupEndServer();
		transportRequestServer = new TransportRequestServer();
		waitRequestServer = new WaitRequestServer();
		jobFinishServer = new JobFinishServer();
		jobBlockServer = new JobBlockServer();
		toolBreakdownServer = new ToolBreakdownServer();
		toolRepairedServer = new ToolRepairedServer();

		addBehaviour(transportRequestServer);
		addBehaviour(waitRequestServer);
		addBehaviour(setupEndServer);
		addBehaviour(jobFinishServer);
		addBehaviour(jobBlockServer);
		addBehaviour(toolBreakdownServer);
		addBehaviour(toolRepairedServer);

	}

	@Override
	protected boolean acceptJobCondition() {
		// TODO Auto-generated method stub
		return (getBufferInfo().size() + getTransportBuffer()) < getBufferInfo().getBufferCapacity();
		// return true;
	}

	@Override
	protected void onTransportRequest(long blockBeginTime) {
		// TODO Auto-generated method stub
		if (isCollectData())
			blockDataset.add(currentTime(), currentTime() - blockBeginTime);
	}

	public void simpleClone(ToolGroup toolGroup) {
		super.simpleClone(toolGroup);
		toolGroup.bufferInfo = bufferInfo;
		toolGroup.toolGroupInfo = toolGroupInfo;
		toolGroup.bufferData = bufferData;
		toolGroup.blockDataset = blockDataset;
		// toolGroup.decisionMaker = decisionMaker;
		toolGroup.release = release;
		toolGroup.transportRequestServer = transportRequestServer;
		toolGroup.waitRequestServer = waitRequestServer;
		toolGroup.jobFinishServer = jobFinishServer;
		toolGroup.jobBlockServer = jobBlockServer;
		toolGroup.toolBreakdownServer = toolBreakdownServer;
		toolGroup.toolRepairedServer = toolRepairedServer;
		toolGroup.random = random;
		toolGroup.setCollectData(isCollectData());
	}

	@Override
	protected void onWaitRequest(JobInfo jobInfo) {
		// TODO Auto-generated method stub
		int preBufferSize = getBufferInfo().size();
		getBufferInfo().offer(jobInfo);
		// System.out.println(getAdditionalString()+",job enters buffer "+jobInfo.getName()+", toll:"+this.getName());

		ArrayList<ToolInfo> freeTool = toolGroupInfo.getFreeTools();
		if (freeTool.size() > 0) {
			int index = (int) (random.nextDouble() * freeTool.size());
			startProcess(freeTool.get(index));
		}
		if (getBufferInfo().size() != preBufferSize && isCollectData()) {
			bufferData.add(currentTime(), getBufferInfo().size() + getTransportBuffer());
		}

	}

	public int setupOrProcess(ToolInfo toolInfo, String[] receivers) {
		double setupTime = toolInfo.getSetupTime();

		if (setupTime > 0) {
			toolInfo.state = ToolState.SETUP;
			Activation msg = new Activation((long) (currentTime() + setupTime), getAgentName(), CustomActivation.SetupEnd);
			msg.setContent(String.valueOf(toolInfo.toolIndex));
			msg.setObjectContent(receivers);
			sendSimulationMsg(msg);

			if (isCollectData()) {
				toolInfo.toolData.freeTime += currentTime() - toolInfo.freeBeginTime;
				toolInfo.toolData.addFreeTime(toolInfo.freeBeginTime, currentTime() - toolInfo.freeBeginTime);
				toolInfo.setupBeginTime = currentTime();
				toolInfo.toolData.addSetupTime(currentTime(), currentTime() + setupTime);
			}
			return 1;
		} else {// no setup,start process directly

			toolInfo.state = ToolState.BUSY;
			sendMessage(receivers, "Start Process", String.valueOf(toolInfo.toolIndex));
			if (isCollectData()) {
				toolInfo.processBeginTime = currentTime();
				toolInfo.toolData.freeTime += currentTime() - toolInfo.freeBeginTime;
				toolInfo.toolData.addFreeTime(toolInfo.freeBeginTime, currentTime() - toolInfo.freeBeginTime);
			}

			return 2;
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<JobInfo> a(ToolInfo toolInfo, int bufferState) {
		ArrayList<JobInfo> job = new ArrayList<JobInfo>();
		if (getSubSimulation() != null) {
			synchronized (getSubSimulation().isRunning()) {
				if (getSubSimulation() != null && !getSubSimulation().isRunning() && bufferInfo.size() > 1 && this.getDecisionAgent().isEmpty()) {
					getSubSimulation().setRunning(true);
					getSubSimulation().pauseMainSimulation();
					getSubSimulation().setHistoryDecisions();
					System.out.println(getAgentName() + " is making decisions, alternatives: " + bufferInfo.getJobs());
					AgentBasedModel clonedAgentModel = null;
					DataMeta<JobInfo> data = null;
					if (getSubSimulation().isCollectData()) {
						clonedAgentModel = getSubSimulation().cloneAgentModel();
						data = new DataMeta<JobInfo>(clonedAgentModel);
						data.setLocation(clonedAgentModel.getToolGroupAgent(getName()).getToolGroupInfo().getToolInfos()[toolInfo.getToolIndex()]);
						getSubSimulation().getTrainingDataset().addSequencingData(getAgentName(), data);
					}

					ArrayList<JobInfo> bestJobs = new ArrayList<JobInfo>();
					double min = Double.MAX_VALUE;
					for (int i = bufferInfo.size() - 1; i > -1; i--) {
						JobInfo jobInfo = bufferInfo.getJobsinBuffer().get(i);
						getSubSimulation().clone().run(this, jobInfo);
						Message msg = this.blockingReceive("Subsimulation Done");
						System.out.println(msg.getObjectContent().toString());
						if (getSubSimulation().isCollectData()) {
							data.addActionAndValue(clonedAgentModel.getJobAgent(jobInfo.getName()).getJobInfo(), (Double) msg.getObjectContent());
						}
						if (min > (Double) msg.getObjectContent()) {
							min = (Double) msg.getObjectContent();
							bestJobs.clear();
							bestJobs.add(bufferInfo.getJobsinBuffer().get(i));
						} else if (min == (Double) msg.getObjectContent()) {
							bestJobs.add(bufferInfo.getJobsinBuffer().get(i));
						}
					}
					if (bestJobs.size() == 0) {
						System.out.println("=================================Error=======================");
					} else {
						job = new ArrayList<JobInfo>();
						job.add(bestJobs.get(0));
						bufferInfo.getWaitDataset().add(job.get(0).getName(), currentTime(), currentTime() - job.get(0).getStateStartTime());
						if (0 == bufferInfo.remove(job.get(0).getName())) {
							System.out.println(getAdditionalString() + " ," + getAgentName() + " remove job " + job.get(0).getName()
									+ " from buffer error, jobs in buffer: " + bufferInfo.getJobs() + ",last message: "
									+ this.getLastReceivedMessage().toString());
							job = null;
							System.exit(0);
						} else {
							// System.out.println(job.get(0).getName()+" removed from buffer "+getName());
						}
					}
					System.out.println(getAgentName() + " decision is made, selected job:" + (job == null ? "" : job.get(0).getName()));
					getHistoryDecisions().add(job);
					getSubSimulation().setRunning(false);
					getSubSimulation().continueMainSimulation();

				} else {

					job = getBufferInfo().poll(toolInfo, currentTime(), bufferState);
					// System.out.println(getAdditionalString()+", selected job by rule:"
					// +job.get(0).getName()+" on tool "+getName());
					getHistoryDecisions().add(job);
				}
			}

		} else if (getSubSimulation() == null && getHistoryDecisionIndex() < getHistoryDecisions().size()) {
			job = (ArrayList<JobInfo>) getHistoryDecisions().get(getHistoryDecisionIndex());
			setHistoryDecisionIndex(getHistoryDecisionIndex() + 1);
			bufferInfo.remove(job.get(0).getName());
			System.out.println(getAdditionalString() + "  ," + getAgentName() + ",Select job by history decsion for job, selected job:" + job.get(0).getName());
		} else if (getSubSimulation() == null) {
			// //subsimulation, specified by the subsimulation
			if (getSubSimulation() == null && this.getDecisionAgent().equals(this.getAgentName()) && !this.isHasMadeDecision() && this.getDecision() != null) {
				job = new ArrayList<JobInfo>();
				job.add((JobInfo) this.getDecision());
				this.setHasMadeDecision(true);
				getBufferInfo().getWaitDataset().add(job.get(0).getName(), currentTime(), currentTime() - job.get(0).getStateStartTime());

				System.out.println(getAdditionalString() + ", selected job:" + job.get(0).getName() + " on tool " + getAgentName());
				if (0 == bufferInfo.remove(job.get(0).getName())) {
					System.out.println(getAdditionalString() + " ," + getAgentName() + " remove job " + job.get(0).getName()
							+ " from buffer error, jobs in buffer: " + bufferInfo.getJobs() + ",last message: " + this.getLastReceivedMessage().toString());
					job = null;
					System.exit(0);
				} else {
					// System.out.println(job.get(0).getName()+" removed from buffer "+getName());
				}

			} else {
				job = getBufferInfo().poll(toolInfo, currentTime(), bufferState, DispatchingRules.LPT);
				// System.out.println(getAdditionalString()+", selected job by rule:"
				// +job.get(0).getName()+" on tool "+getName());
			}
		}
		return job;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<JobInfo> b(ToolInfo toolInfo, int bufferState) {
		ArrayList<JobInfo> job = new ArrayList<JobInfo>();
		if (getSubSimulation() != null) {

			job = getBufferInfo().poll(toolInfo, currentTime(), bufferState);
			// System.out.println(getAdditionalString()+", selected job by rule:"
			// +job.get(0).getName()+" on tool "+getName());

			getHistoryDecisions().add(job);

		} else if (getSubSimulation() == null && getHistoryDecisionIndex() < getHistoryDecisions().size()) {
			job = (ArrayList<JobInfo>) getHistoryDecisions().get(getHistoryDecisionIndex());

			setHistoryDecisionIndex(getHistoryDecisionIndex() + 1);
			bufferInfo.remove(job.get(0).getName());
			// System.out.println(getAdditionalString()+"  ,"+getName()+",Select job by history decsion for job, selected job:"+job.get(0).getName());
		} else if (getSubSimulation() == null) {

			job = getBufferInfo().poll(toolInfo, currentTime(), bufferState);
			// System.out.println(getAdditionalString()+", selected job by rule:"
			// +job.get(0).getName()+" on tool "+getName());

		}
		return job;
	}

	public ArrayList<JobInfo> c(ToolInfo toolInfo, int batchID) {
		return getBufferInfo().poll(toolInfo, currentTime(), batchID, DispatchingRules.LPT);
	}

	public ArrayList<JobInfo> d(ToolInfo toolInfo, int batchID) {

		if (bufferInfo.size() < 2) {

			return getBufferInfo().poll(toolInfo, currentTime(), batchID);
		}

		// AgentBasedModel clonedAgentModel =
		// getSubSimulation().cloneAgentModel();
		double min = Double.MAX_VALUE;
		JobInfo bestJob = null;

		for (int i = bufferInfo.size() - 1; i > -1; i--) {
			JobInfo jobInfo = bufferInfo.getJobsinBuffer().get(i);
			SequencingTrainingData data = new SequencingTrainingData();
			GlobalDataCompute compute = new GlobalDataCompute(getSubSimulation().getAgentModel());
			compute.getGlobalData(data, currentTime());
			data.setLocalData(jobInfo, bufferInfo, toolInfo, getSubSimulation().getAgentModel(), currentTime());
			int index = toolGroupInfo.getDisicionMaker().getKmeans().patternRecognition(data.clone());
			double value = toolGroupInfo.getDisicionMaker().getBpNetworks().get(index).predict(data.clone())[0];
			if (value < min) {
				min = value;
				bestJob = jobInfo;
			}
		}

		ArrayList<JobInfo> job = new ArrayList<JobInfo>();
		job.add(bestJob);
		bufferInfo.getWaitDataset().add(job.get(0).getName(), currentTime(), currentTime() - job.get(0).getStateStartTime());
		if (0 == bufferInfo.remove(job.get(0).getName())) {
			System.out.println(getAdditionalString() + " ," + getAgentName() + " remove job " + job.get(0).getName() + " from buffer error, jobs in buffer: "
					+ bufferInfo.getJobs() + ",last message: " + this.getLastReceivedMessage().toString());
			job = null;
			System.exit(0);
		} else {
			// System.out.println(job.get(0).getName()+" removed from buffer "+getName());
		}

		return job;

	}


	
	@SuppressWarnings("unchecked")
	private ArrayList<JobInfo> e(ToolInfo toolInfo, int bufferState) {
		ArrayList<JobInfo> job = new ArrayList<JobInfo>();
		if (getSubSimulation() != null) {
			synchronized (getSubSimulation().isRunning()) {
				if (getSubSimulation() != null && !getSubSimulation().isRunning() && bufferInfo.size() > 1 && this.getDecisionAgent().isEmpty()) {
					getSubSimulation().setRunning(true);
					getSubSimulation().pauseMainSimulation();
					getSubSimulation().setHistoryDecisions();
					System.out.println(getAgentName() + " is making decisions, alternatives: " + bufferInfo.getJobs());
					AgentBasedModel clonedAgentModel = null;
					DataMeta<JobInfo> data1 = null;
					if (getSubSimulation().isCollectData()) {
						clonedAgentModel = getSubSimulation().cloneAgentModel();
						data1 = new DataMeta<JobInfo>(clonedAgentModel);
						data1.setLocation(clonedAgentModel.getToolGroupAgent(getName()).getToolGroupInfo().getToolInfos()[toolInfo.getToolIndex()]);
						getSubSimulation().getTrainingDataset().addSequencingData(getAgentName(), data1);
					}

					ArrayList<JobInfo> bestJobs = new ArrayList<JobInfo>();
					double max = Double.MIN_VALUE;
					for (int i = bufferInfo.size() - 1; i > -1; i--) {
						JobInfo jobInfo = bufferInfo.getJobsinBuffer().get(i);
						getSubSimulation().clone().run(this, jobInfo);
						Message msg = this.blockingReceive("Subsimulation Done");
						System.out.println(msg.getObjectContent().toString());
					
						SequencingTrainingData data = new SequencingTrainingData();
						GlobalDataCompute compute = new GlobalDataCompute(getSubSimulation().getAgentModel());
						compute.getGlobalData(data, currentTime());
						data.setLocalData(jobInfo, bufferInfo, toolInfo, getSubSimulation().getAgentModel(), currentTime());
						int index = toolGroupInfo.getDisicionMaker().getKmeans().patternRecognition(data.clone());
						double value = toolGroupInfo.getDisicionMaker().getBpNetworks().get(index).predict(data.clone())[0];
						
						
						Object[] objs=(Object[]) msg.getObjectContent();
						AgentBasedModel futureState=(AgentBasedModel) objs[0];
						ArrayList<JobInfo> futureActions=(ArrayList<JobInfo>) objs[1];
						BufferInfo futureBufferInfo=(BufferInfo) objs[2];
						ToolInfo futureToolInfo=(ToolInfo) objs[3];
						long futureTime=(Long) objs[4];
						double maxFutureValue=Double.MIN_VALUE;
						for(JobInfo futureAct:futureActions){
							SequencingTrainingData fdata = new SequencingTrainingData();
							GlobalDataCompute fcompute = new GlobalDataCompute(futureState);
							fcompute.getGlobalData(fdata, currentTime());
							fdata.setLocalData(futureAct, futureBufferInfo, futureToolInfo, futureState, futureTime);
							int findex = toolGroupInfo.getDisicionMaker().getKmeans().patternRecognition(fdata.clone());
							double fvalue = toolGroupInfo.getDisicionMaker().getBpNetworks().get(findex).predict(fdata.clone())[0];
							if(maxFutureValue<fvalue){
								maxFutureValue=fvalue;
							}
						}
						double discountRate=0.5;
						double stepRate=0.2;
						GlobalDataCompute fcompute = new GlobalDataCompute(futureState);
						double futureReward=1/fcompute.getCurrentWip();
						value=value+stepRate*(futureReward+discountRate*maxFutureValue-value);
						
						if (max <value) {
							max = value;
							bestJobs.clear();
							bestJobs.add(bufferInfo.getJobsinBuffer().get(i));
						} else if (max == value) {
							bestJobs.add(bufferInfo.getJobsinBuffer().get(i));
						}

						if (getSubSimulation().isCollectData()) {
							data1.addActionAndValue(clonedAgentModel.getJobAgent(jobInfo.getName()).getJobInfo(), value);
							
							if (getSubSimulation().getTrainingDataset().getSequencingDataset().get(getAgentName()).size() > 100) {
								train();
								getSubSimulation().getTrainingDataset().getSequencingDataset().get(getAgentName()).clear();
							}
						}
					}
					if (bestJobs.size() == 0) {
						System.out.println("=================================Error=======================");
					} else {
						job = new ArrayList<JobInfo>();
						job.add(bestJobs.get(0));
						bufferInfo.getWaitDataset().add(job.get(0).getName(), currentTime(), currentTime() - job.get(0).getStateStartTime());
						if (0 == bufferInfo.remove(job.get(0).getName())) {
							System.out.println(getAdditionalString() + " ," + getAgentName() + " remove job " + job.get(0).getName()
									+ " from buffer error, jobs in buffer: " + bufferInfo.getJobs() + ",last message: "
									+ this.getLastReceivedMessage().toString());
							job = null;
							System.exit(0);
						} else {
							// System.out.println(job.get(0).getName()+" removed from buffer "+getName());
						}
					}
					System.out.println(getAgentName() + " decision is made, selected job:" + (job == null ? "" : job.get(0).getName()));
					getHistoryDecisions().add(job);
					getSubSimulation().setRunning(false);
					getSubSimulation().continueMainSimulation();

				} else {

					job = getBufferInfo().poll(toolInfo, currentTime(), bufferState);
					// System.out.println(getAdditionalString()+", selected job by rule:"
					// +job.get(0).getName()+" on tool "+getName());
					getHistoryDecisions().add(job);
				}
			}

		} else if (getSubSimulation() == null && getHistoryDecisionIndex() < getHistoryDecisions().size()) {
			job = (ArrayList<JobInfo>) getHistoryDecisions().get(getHistoryDecisionIndex());
			setHistoryDecisionIndex(getHistoryDecisionIndex() + 1);
			bufferInfo.remove(job.get(0).getName());
			System.out.println(getAdditionalString() + "  ," + getAgentName() + ",Select job by history decsion for job, selected job:" + job.get(0).getName());
		} else if (getSubSimulation() == null) {
			// //subsimulation, specified by the subsimulation
			if ( this.getDecisionAgent().equals(this.getAgentName()) && !this.isHasMadeDecision() && this.getDecision() != null) {
				job = new ArrayList<JobInfo>();
				job.add((JobInfo) this.getDecision());
				this.setHasMadeDecision(true);
				getBufferInfo().getWaitDataset().add(job.get(0).getName(), currentTime(), currentTime() - job.get(0).getStateStartTime());

				System.out.println(getAdditionalString() + ", selected job:" + job.get(0).getName() + " on tool " + getAgentName());
				if (0 == bufferInfo.remove(job.get(0).getName())) {
					System.out.println(getAdditionalString() + " ," + getAgentName() + " remove job " + job.get(0).getName()
							+ " from buffer error, jobs in buffer: " + bufferInfo.getJobs() + ",last message: " + this.getLastReceivedMessage().toString());
					job = null;
					System.exit(0);
				} else {
					// System.out.println(job.get(0).getName()+" removed from buffer "+getName());
				}

			} else if( this.getDecisionAgent().equals(this.getAgentName()) && this.isHasMadeDecision() && this.getDecision() != null){
				
			}
			
			else {
				job = getBufferInfo().poll(toolInfo, currentTime(), bufferState, DispatchingRules.SPT);
				// System.out.println(getAdditionalString()+", selected job by rule:"
				// +job.get(0).getName()+" on tool "+getName());
			}
		}
		return job;
	}

	private void train() {
		
		TrainingDataset<SequencingTrainingData> trainingDataset = getSubSimulation().getTrainingDataset().createSequencingTrainingDataset(getAgentName());
		Training<SequencingTrainingData> training=new Training<SequencingTrainingData>();
		DecisionMaker<SequencingTrainingData> decsionMaker = training.oneGroupTrain(trainingDataset);		
		toolGroupInfo.setDecisionMaker(decsionMaker);


	}

	private int startProcess(ToolInfo toolInfo) {

		Message msg1 = receive("Wait Command");
		if (msg1 != null) {

			setTransportBuffer(getTransportBuffer() - 1);
			onWaitRequest((JobInfo) msg1.getObjectContent());
			return 0;

		}

		int batchID = getBufferInfo().validateSize();
		if (!toolInfo.IsFree() || batchID == -1) {
			// System.out.println(getAdditionalString()+", tool:"+this.getName()+", Try start, but failure");
			return 0;
		}

		ArrayList<JobInfo> job = null;

		job = a(toolInfo, batchID);

		toolInfo.currentBatchID = batchID;
		toolInfo.currentJobType = job.get(0).getProductName();
		toolInfo.currentJobStepName = job.get(0).getCurrentProcessName();
		toolInfo.processedJobNum = job.size();
		String[] receivers = new String[job.size()];
		String str = "";
		for (int i = 0; i < job.size(); i++) {
			receivers[i] = job.get(i).getName();
			str = job.get(i).getName() + ",";
		}
		toolInfo.currentJobs = str.substring(0, str.length() - 1);
		int temp = setupOrProcess(toolInfo, receivers);

		if (receivers.length > 1) {
			System.exit(0);
		}

		return temp;
	}

	protected class SetupEndServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			Message msg = receive(CustomActivation.SetupEnd);
			if (msg != null) {
				ToolInfo toolInfo = toolGroupInfo.toolInfo(Integer.valueOf(msg.getContent()));
				debugMsg("setup ends, start processing job " + ((String[]) msg.getObjectContent())[0]);
				sendMessage((String[]) msg.getObjectContent(), "Start Process", msg.getContent());
				toolInfo.state = ToolState.BUSY;

				if (isCollectData()) {
					toolInfo.processBeginTime = currentTime();
					toolInfo.toolData.setupTime += currentTime() - toolInfo.setupBeginTime;
				}

			}
			return 1;

		}

	}

	private class JobFinishServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			Message reply = receive("Job Finish");
			if (reply != null) {
				String str[] = reply.getContent().split("-");
				ToolInfo toolInfo = toolGroupInfo.toolInfo(Integer.valueOf(str[0]));

				toolInfo.finishedJobNum++;
				// bottel
				// toolGroupInfo.bottleneck.finishedOneJob(Integer.valueOf(str[1]));
				//
				if (toolInfo.finishedJobNum == toolInfo.processedJobNum) {
					// tool will be free only all of jobs in it left
					if (isCollectData()) {
						if (toolInfo.state == ToolState.BLOCK) {
							toolInfo.toolData.blockTime += currentTime() - toolInfo.blockBeginTime;
							toolInfo.toolData.addBlockTime(toolInfo.blockBeginTime, currentTime());
						} else {
							toolInfo.toolData.processTime += currentTime() - toolInfo.processBeginTime;
							toolInfo.toolData.addProcessTime(toolInfo.processBeginTime, currentTime(), toolInfo.currentJobs);
						}
					}
					toolInfo.preJobType = toolInfo.currentJobType;
					toolInfo.preJobStepName = toolInfo.currentJobStepName;
					toolInfo.preBatchID = toolInfo.currentBatchID;
					toolInfo.state = ToolState.FREE;
					toolInfo.currentJobs = "";
					toolInfo.freeBeginTime = currentTime();
					toolInfo.toolLife++;
					toolInfo.finishedJobNum = 0;
					if (toolInfo.waitInterrupt != null) {
						handleWaitingInterrupt(toolInfo);
					} else {
						int temp = startProcess(toolInfo);
						if (temp != 0 && isCollectData()) {
							bufferData.add(currentTime(), getBufferInfo().size() + getTransportBuffer());
							// System.out.println(name+" starts , because of completion "+currentTime()+","+reply.getSender());
						}
					}
				}

			}
			return 1;
		}

	}

	private class JobBlockServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {

			Message reply = receive("Job Block");
			if (reply != null) {
				ToolInfo toolInfo = toolGroupInfo.toolInfo(Integer.valueOf(reply.getContent()));
				toolInfo.state = ToolState.BLOCK;
				if (isCollectData()) {
					toolInfo.blockBeginTime = currentTime();
					toolInfo.toolData.processTime += currentTime() - toolInfo.processBeginTime;
				}
			}
			return 1;
		}
	}

	private void handleWaitingInterrupt(ToolInfo toolInfo) {
		Interrupt interrupt = toolInfo.waitInterrupt;
		changeStatebyInterrupt(interrupt, toolInfo);
		String content = "";
		if (interrupt.getRange().equals("ToolGroup")) {
			content = interrupt.applyTo + "%" + interrupt.index + "%" + toolInfo.toolIndex;
		} else if (interrupt.getRange().equals("Tool")) {
			content = "SingleTool" + "%" + toolInfo.toolIndex + "%" + interrupt.index;
		} else {
			System.out.println("handle waiting interrupt error");
			System.exit(0);
		}
		Activation msg = new Activation((currentTime() + interrupt.recoveryTime()), getAgentName(), CustomActivation.Recovery);
		msg.setContent(content);
		sendSimulationMsg(msg);
		toolInfo.waitInterrupt = null;
	}

	private class ToolBreakdownServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {

			Message reply = receive(CustomActivation.Interrupt);
			if (reply != null) {
				ToolInfo toolInfo = null;
				String content = "";
				Interrupt interrupt = null;
				String temp[] = reply.getContent().split("%");

				if (temp[0].equals("OneTool")) {
					Random r = new Random(System.currentTimeMillis());
					int index = r.nextInt(toolGroupInfo.getToolNuminGroup());
					toolInfo = toolGroupInfo.toolInfo(index);
					content = temp[0] + "%" + temp[1] + "%" + index;
					interrupt = toolGroupInfo.getInterruptByIndex(Integer.valueOf(temp[1]));
				} else if (temp[0].equals("SingleTool")) {
					toolInfo = toolGroupInfo.toolInfo(Integer.valueOf(temp[1]));
					content = reply.getContent();
					interrupt = toolInfo.getInterruptByIndex(Integer.valueOf(temp[2]));
				} else {
					System.out.println("Interruption server error");
					System.exit(0);
				}

				handleInterrupt(interrupt, toolInfo, content);

				// String temp[] = reply.getContent().split("%");
				// Interrupt interrupt =
				// toolGroupInfo.getInterruptByIndex(Integer
				// .valueOf(temp[1]));
				// String content = "";
				// ToolInfo toolInfo=null;
				// if (temp[0].equals("OneTool")) {
				// Random r = new Random(System.currentTimeMillis());
				// int index = r.nextInt(toolGroupInfo.toolNuminGroup);
				// toolInfo = toolGroupInfo.toolInfo(index);
				// content = temp[0] + "%" + temp[1] + "%" + index;
				//
				// } else if (temp[0].equals("EachTool")) {
				// toolInfo = toolGroupInfo.toolInfo(Integer
				// .valueOf(temp[2]));
				// content=reply.getContent();
				// } else if (temp[0].equals("SpecifyTool")) {
				// toolInfo = toolGroupInfo.toolInfo(Integer
				// .valueOf(temp[2]));
				// content=reply.getContent();
				// }else{
				// System.out.println("handle interruptions error");
				// System.exit(0);
				// }
				// handleInterrupt(interrupt, toolInfo,
				// content);
			}
			return 1;
		}
	}

	private class ToolRepairedServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {

			Message reply = receive(CustomActivation.Recovery);
			if (reply != null) {

				String temp[] = reply.getContent().split("%");

				ToolInfo toolInfo = null;
				if (temp[0].equals("OneTool")) {
					toolInfo = toolGroupInfo.toolInfo(Integer.valueOf(temp[2]));
					Interrupt interrupt = toolGroupInfo.getInterruptByIndex(Integer.valueOf(temp[1]));
					long interrupTime = interrupt.nextOccurAfterTime();
					Activation msg = new Activation(currentTime() + interrupTime, getAgentName(), CustomActivation.Interrupt);
					msg.setContent(temp[0] + "%" + temp[1]);
					sendSimulationMsg(msg);
				} else if (temp[0].equals("SingleTool")) {
					toolInfo = toolGroupInfo.toolInfo(Integer.valueOf(temp[1]));
					Interrupt interrupt = toolInfo.getInterruptByIndex(Integer.valueOf(temp[2]));
					long interrupTime = interrupt.nextOccurAfterTime();
					Activation msg = new Activation((currentTime() + interrupTime), getAgentName(), CustomActivation.Interrupt);
					msg.setContent(reply.getContent());
					sendSimulationMsg(msg);
				} else {
					System.out.println("error on interruption recovery");
					System.exit(0);
				}
				if (isCollectData()) {
					if (toolInfo.state == ToolState.BREAKDOWN) {
						toolInfo.toolData.breakdownTime += currentTime() - toolInfo.breakdownBeginTime;
						toolInfo.toolData.addInterruptTime(toolInfo.breakdownBeginTime, currentTime());
					} else if (toolInfo.state == ToolState.MAINTENANCE) {
						toolInfo.toolData.maintenanceTime += currentTime() - toolInfo.maintenanceBeginTime;
						toolInfo.toolData.addInterruptTime(toolInfo.maintenanceBeginTime, currentTime());
					}
				}
				toolInfo.state = ToolState.FREE;
				toolInfo.freeBeginTime = currentTime();
				int temp1 = startProcess(toolInfo);

				if (temp1 != 0 && isCollectData()) {
					bufferData.add(currentTime(), getBufferInfo().size() + getTransportBuffer());
				}

				// String temp[] = reply.getContent().split("%");
				// Interrupt interrupt =
				// toolGroupInfo.getInterruptByIndex(Integer
				// .valueOf(temp[1]));
				// long interrupTime = interrupt.nextOccurAfterTime();
				//
				// ToolInfo toolInfo =
				// toolGroupInfo.toolInfo(Integer.valueOf(temp[2]));
				// if (temp[0].equals("OneTool")) {
				// Activation msg = new Activation(currentTime()
				// + interrupTime, name, CustomActivation.Interrupt);
				// msg.setContent(temp[0] + "%" + temp[1]);
				// sendSimulationMsg(msg);
				// } else if (temp[0].equals("EachTool")) {
				// Activation msg = new Activation(
				// (currentTime() + interrupTime), name,
				// CustomActivation.Interrupt);
				// msg.setContent(reply.getContent());
				// sendSimulationMsg(msg);
				// } else if (temp[0].equals("SpecifyTool")) {
				// Activation msg = new Activation(
				// (currentTime() + interrupTime), name,
				// CustomActivation.Interrupt);
				// msg.setContent(reply.getContent());
				// sendSimulationMsg(msg);
				// }
				// if (collectData) {
				// if (toolInfo.state == ToolState.BREAKDOWN) {
				// toolInfo.toolData.breakdownTime += currentTime()
				// - toolInfo.breakdownBeginTime;
				// toolInfo.toolData.addInterruptTime(
				// toolInfo.breakdownBeginTime, currentTime());
				// } else if (toolInfo.state == ToolState.MAINTENANCE) {
				// toolInfo.toolData.maintenanceTime += currentTime()
				// - toolInfo.maintenanceBeginTime;
				// toolInfo.toolData.addInterruptTime(
				// toolInfo.maintenanceBeginTime, currentTime());
				// }
				// }
				// toolInfo.state = ToolState.FREE;
				// toolInfo.freeBeginTime = currentTime();
				// int temp1 = startProcess(toolInfo);
				//
				// if (temp1 != 0 && collectData) {
				// bufferData.add(currentTime(), bufferInfo.size()
				// + transportBuffer);
				// }
			}
			return 1;
		}
	}

	@Override
	protected void onSimulatorStart() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSimulatorReset() {
		// TODO Auto-generated method stub
		getBufferInfo().clear();
		setTransportBuffer(0);
		toolGroupInfo.reset();
		// for (int i = 0; i < setupEndServers.length; i++)
		// if (setupEndServers[i] != null)
		// removeBehaviour(setupEndServers[i]);
		send("Simulator", "Reset Confirm", "");
	}

	@Override
	protected void onSimulatorEnd() {
		// TODO Auto-generated method stub
		removeBehaviour(transportRequestServer);
		removeBehaviour(waitRequestServer);

		removeBehaviour(jobFinishServer);
		removeBehaviour(jobBlockServer);
		removeBehaviour(toolBreakdownServer);
		removeBehaviour(toolRepairedServer);

	}

	private void handleInterrupt(Interrupt interrupt, ToolInfo toolInfo, String content) {
		if (toolInfo.isInterrupt() || toolInfo.waitInterrupt != null) {
			return;
		}
		if (toolInfo.IsFree()) {
			changeStatebyInterrupt(interrupt, toolInfo);
			Activation msg = new Activation((currentTime() + interrupt.recoveryTime()), getAgentName(), CustomActivation.Recovery);
			msg.setContent(content);
			sendSimulationMsg(msg);
		} else {
			toolInfo.waitInterrupt = interrupt;
		}
	}

	private void changeStatebyInterrupt(Interrupt interrupt, ToolInfo toolInfo) {
		if (interrupt.interruptType.equals("Breakdown")) {
			toolInfo.state = ToolState.BREAKDOWN;
			if (isCollectData()) {
				toolInfo.breakdownBeginTime = currentTime();
				toolInfo.toolData.freeTime += currentTime() - toolInfo.freeBeginTime;
				toolInfo.toolData.addFreeTime(toolInfo.freeBeginTime, currentTime() - toolInfo.freeBeginTime);
			}
		} else if (interrupt.interruptType.equals("Maintenance")) {
			toolInfo.state = ToolState.MAINTENANCE;
			if (isCollectData()) {
				toolInfo.maintenanceBeginTime = currentTime();
				toolInfo.toolData.freeTime += currentTime() - toolInfo.freeBeginTime;
				toolInfo.toolData.addFreeTime(toolInfo.freeBeginTime, currentTime() - toolInfo.freeBeginTime);
			}
		}
	}

	public ToolGroup(String name, int num) {
		super(name);
		setupEndServer = new SetupEndServer();
		transportRequestServer = new TransportRequestServer();
		waitRequestServer = new WaitRequestServer();
		jobFinishServer = new JobFinishServer();
		jobBlockServer = new JobBlockServer();
		toolBreakdownServer = new ToolBreakdownServer();
		toolRepairedServer = new ToolRepairedServer();

		addBehaviour(transportRequestServer);
		addBehaviour(waitRequestServer);
		addBehaviour(setupEndServer);
		addBehaviour(jobFinishServer);
		addBehaviour(jobBlockServer);
		addBehaviour(toolBreakdownServer);
		addBehaviour(toolRepairedServer);

	}

	public ToolGroup clone(AgentBasedModel agentModel, AgentEnvironment environment, ToolGroupDataset toolGroupDataset, BufferDataset bufferDataset) {
		ToolGroup tg = new ToolGroup(this.getAgentName(), toolGroupInfo.getToolNuminGroup());
		super.clone(tg, environment);
		ToolGroupData toolGroupData = new ToolGroupData(tg.getAgentName(), agentModel.getReleaseAgent().getSimulatorInfo().getDataAcqTime());
		toolGroupDataset.add(toolGroupData, agentModel.getReleaseAgent().getSimulatorInfo().getDataAcqTime());

		tg.bufferData = new BufferData(this.getAgentName());
		bufferDataset.add(tg.bufferData);
		tg.blockDataset = toolGroupData.blockDataset;
		tg.setBufferInfo(getBufferInfo().clone(agentModel, toolGroupData.waitDataset));
		tg.toolGroupInfo = toolGroupInfo.clone(tg.getBufferInfo(), toolGroupData);

		tg.random = random.clone();
		tg.setAdditionalString("Sub Simulation");

		return tg;
	}

	public void reset() {
		getBufferInfo().clear();
		setTransportBuffer(0);
		toolGroupInfo.reset();
		random.reset();
		// for (int i = 0; i < setupEndServers.length; i++)
		// if (setupEndServers[i] != null)
		// removeBehaviour(setupEndServers[i]);
		super.resetAgent();
	}

	// public ArrayList<JobInfo> getBestJob(ToolInfo toolInfo) {
	// TrainingData clusterData = new TrainingData(toolInfo.toolGroupName);
	// clusterData.setCurrentTime(currentTime());
	// Message msg = new Message();
	// msg.setReceiver(getRelease());
	// msg.setTitle("Global Data");
	// send(msg);
	// msg = blockingReceive("Global Data");
	// String temp[] = msg.getContent().split("--");
	// int wip = Integer.parseInt(temp[1]);
	// String[] wipDetailstr = temp[0].split("-");
	// double[] wipDetail = new double[wipDetailstr.length];
	// for (int i = 0; i < wipDetail.length; i++) {
	// wipDetail[i] = Integer.parseInt(wipDetailstr[i]);
	// }
	// clusterData.setWip(wip);
	// clusterData.setWipD(wipDetail);
	//
	// int numofToolGroups = toolGroupInfo.getAllToolGroupName().size();
	// double bufferSize = 0;
	// double bufferTime = 0;
	// double[] bufferSizeD = new double[numofToolGroups - 1];
	// double[] bufferTimeD = new double[numofToolGroups - 1];
	// ;
	// double[] unaTooRatD = new double[numofToolGroups];
	// ;
	// double[] totWaiTimD = new double[numofToolGroups];
	// ;
	// int te = 0;
	// for (int i = 0; i < numofToolGroups; i++) {
	//
	// String tn = toolGroupInfo.getAllToolGroupName().get(i);
	// if (tn.equals(toolGroupInfo.toolGroupName)) {
	// bufferSize = getBufferInfo().size();
	// bufferTime = getBufferInfo().getBufferTime();
	// unaTooRatD[i] = toolGroupInfo.getUnavailableToolRatioD();
	// totWaiTimD[i] = getBufferInfo().getTotalWaitingTime(
	// currentTime());
	// te = 1;
	// } else {
	// msg.setReceiver(tn);
	// send(msg);
	// msg = blockingReceive("Global Data");
	// temp = msg.getContent().split("-");
	// bufferSizeD[i - te] = Integer.valueOf(temp[0]);
	// bufferTimeD[i - te] = Double.valueOf(temp[1]);
	// unaTooRatD[i] = Double.valueOf(temp[2]);
	// totWaiTimD[i] = Double.valueOf(temp[3]);
	//
	// for (JobInfo job : getBufferInfo().getJobsinBuffer()) {
	// // if(tn.equals(job.nextToolGroupName)){
	// // job.setNextBufferSize(Integer.valueOf(temp[0]));
	// // job.setNextBufferTime(Double.valueOf(temp[1]));
	// // job.setNextBreakdownP(Double.valueOf(temp[2]));
	// // }
	// }
	// }
	//
	// }
	// clusterData.setBuffersize(bufferSize);
	// clusterData.setBufferTime(bufferTime);
	// clusterData.setBuffersizeD(bufferSizeD);
	// clusterData.setBufferTimeD(bufferTimeD);
	// clusterData.setUnaTooRatD(unaTooRatD);
	// clusterData.setTotWaiTimD(totWaiTimD);
	//
	// TrainingData[] bpData = new TrainingData[getBufferInfo().size()];
	// int index = 0;
	// for (JobInfo job : getBufferInfo().getJobsinBuffer()) {
	// // job.setWip( (int) wipDetail[job.getJobTypeIndex()]);
	// // job.setSetupTime(toolInfo.getSetupTime(job));
	// // bpData[index]=clusterData.clone();
	// // bpData[index].setData(job, bufferInfo, wip, currentTime);
	// // index++;
	//
	// }
	//
	// int rjob = dispatcher.getBestJob(clusterData, bpData);
	//
	// if (rjob == -1)
	// return null;
	// JobInfo selectedJOb = getBufferInfo().getJobsinBuffer().get(rjob);
	//
	// ArrayList<JobInfo> jobs = new ArrayList<JobInfo>();
	// jobs.add(selectedJOb);
	// return jobs;

	// }

	// public DecisionMaker<SequencingTrainingData> getDispatcher() {
	// return decisionMaker;
	// }

	public BufferInfo getBufferInfo() {
		return bufferInfo;
	}

	public void setBufferInfo(BufferInfo bufferInfo) {
		this.bufferInfo = bufferInfo;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public ToolGroupInfo getToolGroupInfo() {
		return toolGroupInfo;
	}

	public void setToolGroupInfo(ToolGroupInfo toolGroupInfo) {
		this.toolGroupInfo = toolGroupInfo;
	}

	private void debugMsg(String str) {
		// System.out.println(getAdditionalString()+", tool group: "+getName()+", time:"+currentTime()+","+str);
	}
}