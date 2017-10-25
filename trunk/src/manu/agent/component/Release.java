package manu.agent.component;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import manu.agent.AgentBasedModel;
import manu.model.ManufactureModel;
import manu.model.component.Product;
import manu.model.component.ReleasePolicy;
import manu.scheduling.SubSimulation;
import manu.scheduling.training.data.DataMeta;
import manu.simulation.CustomActivation;
import manu.simulation.SimEndCondition;
import manu.simulation.info.SimulationInfo;
import manu.simulation.result.data.JobData;
import manu.simulation.result.data.JobDataset;
import manu.simulation.result.data.ReleaseDataset;
import simulation.framework.Activation;
import simulation.framework.AgentEnvironment;
import simulation.framework.Behaviour;
import simulation.framework.Message;

public class Release extends SimAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentBasedModel agentModel;
	private Hashtable<String, Integer> blockJob;
	private boolean outofMemory = false;

	private SimulationInfo simulatorInfo;
	private Product[] products;
	private Random random = new Random(100);
	private int maxReleaseBufferSize = 50000;
	private double[] releaseProbRanges;
	private ReleasePolicy releasePolicy;
	private int releaseJobNum = 0;
	private int[] releaseNumByType = null;
	private int wip = 0;
	private int[] wipDetail;
	private boolean wipReachConst = false;
	private boolean[] wipReachConstDetail;

	private ReleaseDataset releaseDataset;
	private JobDataset jobDataset;

	private ReleaseJobServer releaseJobServer;
	private BlockRequestServer blockRequestServer;
	private UnBlockquestServer unBlockquestServer;
	private FinishquestServer finishquestServer;
	private GlobalDatarequestServer globalDatarequestServer;

	public Release(String agentName, Object[] args) {
		super(agentName);
		simulatorInfo = (SimulationInfo) args[0];
		releaseDataset = (ReleaseDataset) args[2];
		jobDataset = (JobDataset) args[3];
		agentModel = (AgentBasedModel) args[4];
		ManufactureModel manuModel = (ManufactureModel) args[1];
		setProducts(manuModel.getProducts());
		releasePolicy = manuModel.getPolicy();
		setSubSimulation((SubSimulation) args[5]);

		this.setAdditionalString("Main Simulation");

		init();

		releaseJobServer = new ReleaseJobServer();
		blockRequestServer = new BlockRequestServer();
		unBlockquestServer = new UnBlockquestServer();
		finishquestServer = new FinishquestServer();
		globalDatarequestServer = new GlobalDatarequestServer();
		addBehaviour(releaseJobServer);
		addBehaviour(blockRequestServer);
		addBehaviour(unBlockquestServer);
		addBehaviour(finishquestServer);
		addBehaviour(globalDatarequestServer);

	}

	private void init() {

		releaseNumByType = new int[getProducts().length];
		wipReachConstDetail = new boolean[getProducts().length];
		wipDetail = new int[getProducts().length];

		if (releasePolicy.isPolicy(ReleasePolicy.ConstantTime)
				|| releasePolicy.isPolicy(ReleasePolicy.ConstantWIP)
				|| releasePolicy.isPolicy(ReleasePolicy.Bottleneck)) {
			releaseProbRanges = new double[getProducts().length];
			double sum = 0;
			for (int j = 0; j < getProducts().length; j++)
				sum += getProducts()[j].getReleaseProb();
			releaseProbRanges[0] = getProducts()[0].getReleaseProb() / sum;
			for (int i = 1; i < getProducts().length; i++) {
				releaseProbRanges[i] = releaseProbRanges[i - 1]
						+ getProducts()[i].getReleaseProb() / sum;
			}
		}
		blockJob = new Hashtable<String, Integer>(2, (float) 0.8);
	}

	private class ReleaseJobServer extends Behaviour {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {

			Message msg = receive("Job Release");
			if (msg != null) {
				String temp = msg.getContent();

				int jobTypeIndex = 0;
				if (msg.getContent().equals("")) {
					jobTypeIndex = 0;
				} else {
					jobTypeIndex = Integer.valueOf(temp);
				}
				int releaseNum = 1;
				if (msg.getObjectContent() != null) {
					releaseNum = (Integer) msg.getObjectContent();
				}
				if (getSubSimulation() == null) {
					// System.out.println("Sub simulation, release job number:"+releaseNum+", index:"+temp+", Time:"+currentTime());
				}//else{
				
				//////////////
				//releaseNum=getReleaseNumber(jobTypeIndex);
				////////////////
				//}
				
				if ((blockJob.size() < maxReleaseBufferSize)) {
					releaseJob(jobTypeIndex, releaseNum);
				}
				createNewReleaseActivation(jobTypeIndex);
			}
			return 1;
		}
	}

	private void releaseJob(int index, int num) {
		if (outofMemory)
			return;
		int jobTypeIndex = 0;
		if (releasePolicy.isPolicy(ReleasePolicy.PlanLotAndDueDate)) {
			// JobData jobdata = new JobData(
			// products[releaseJobNum].getProcessNum());
			// jobDataset.add(products[releaseJobNum].getIndex(), jobdata);
			// Job joba = new Job(products[releaseJobNum].getJobName(), new
			// Object[] {
			// name, jobdata, products[releaseJobNum], currentTime() });
			// environment.register(joba);
			// joba.start();
			// agentModel.addJob(joba);
		} else {// end by plan time

			if (releasePolicy.isPolicy(ReleasePolicy.ConstantTimeDetail)
					|| releasePolicy.isPolicy(ReleasePolicy.ConstantWIPDetail)
					|| releasePolicy.isPolicy(ReleasePolicy.IntelligentControl)) {
				jobTypeIndex = index;
			} else if (releasePolicy.isPolicy(ReleasePolicy.ConstantTime)
					|| releasePolicy.isPolicy(ReleasePolicy.ConstantWIP)) {
				double rnd = random.nextFloat();
				for (int i = 0; i < releaseProbRanges.length; i++) {
					if (rnd <= releaseProbRanges[i]) {
						jobTypeIndex = i;
						break;
					}
				}
			}

			for (int i = 0; i < num; i++) {
				releaseNumByType[jobTypeIndex]++;
				JobData jobdata = new JobData(getProducts()[jobTypeIndex]
						.getProcessFlow().size());
				jobDataset.add(jobTypeIndex, jobdata);
				String jobName = getProducts()[jobTypeIndex].getName()
						+ String.format("%05d", releaseNumByType[jobTypeIndex]);
				try {
					Job joba = new Job(jobName, new Object[] { getAgentName(),
							jobdata, getProducts()[jobTypeIndex],
							currentTime(), this.getAdditionalString() });
					getEnvironment().register(joba);
					agentModel.addJobAgent(joba);

					joba.start();
				} catch (OutOfMemoryError e) {
					// e.printStackTrace();
					outofMemory = true;
					System.out.println("Out of Memory, stop releasing "
							+ agentModel.getJobAgents().size());
				}
			}

		}

		releaseJobNum += num;
		wip += num;
		getWipDetail()[jobTypeIndex] += num;
		if (isCollectData()) {

			releaseDataset.addWIP(jobTypeIndex, currentTime(),
					getWipDetail()[jobTypeIndex], wip);
			for (int i = 0; i < num; i++) {
				releaseDataset.addReleaseTime(jobTypeIndex, currentTime());
			}
		}
	}

	private class BlockRequestServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {

			Message msg = receive("Block");
			if (msg != null) {
				blockJob.put(msg.getSender(), 0);
			}
			return 1;
		}
	}

	private class UnBlockquestServer extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {

			Message msg = receive("UnBlock");
			if (msg != null) {
				blockJob.remove(msg.getSender());

			}
			return 1;
		}
	}

	private class FinishquestServer extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {

			Message msg = receive("Finish");
			if (msg != null) {
				String temp[] = msg.getContent().split("a");

				int jobTypeIndex = Integer.valueOf(temp[0]);
				long cycleTime = Long.valueOf(temp[1]);
				wip--;
				getWipDetail()[jobTypeIndex]--;
				if (isCollectData()) {
					releaseDataset.addCycleTime(jobTypeIndex, currentTime(),
							1.0 * cycleTime);
					releaseDataset.addWIP(jobTypeIndex, currentTime(),
							getWipDetail()[jobTypeIndex], wip);
				}
				if (getSubSimulation() != null) {
					System.out.println(getAdditionalString() + " job finished "
							+ msg.getSender() + ", time: " + currentTime());
				}
				agentModel.removeJobAgent(msg.getSender());
				conwipControl(jobTypeIndex);
				endConditionMeetOrNot();
			}
			return 1;
		}
	}

	private class GlobalDatarequestServer extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {

			Message msg = receive("Global Data");
			if (msg != null) {
				String content = "";
				for (int w : getWipDetail()) {
					content += w + "-";
				}
				content += "-" + wip;
				Message reply = msg.createReply();
				reply.setContent(content);
				send(reply);
			}
			return 1;
		}
	}

	@Override
	protected void onSimulatorStart() {
		// TODO Auto-generated method stub
		// simulatorInfo.StartSimulate();

	}

	@Override
	protected void onSimulatorReset() {
		// TODO Auto-generated method stub
		wip = 0;
		blockJob.clear();
		releaseJobNum = 0;
		for (int i = 0; i < agentModel.getJobAgents().size(); i++) {
			if (1 == sendMessage(agentModel.getJobAgent(i).getAgentName(),
					"Simulate End", "Destroy")) {
				Message msg = receive("Reset Confirm");
				while (msg == null
						|| !msg.getSender().equals(
								agentModel.getJobAgent(i).getAgentName())) {
					msg = receive("Reset Confirm");
				}
			}
		}
		// currentJob.clear();
		agentModel.getJobAgents().clear();
		for (int i = 0; i < releaseNumByType.length; i++) {
			releaseNumByType[i] = 0;
			wipReachConstDetail[i] = false;
			getWipDetail()[i] = 0;
		}
		wipReachConst = false;

		send("Simulator", "Reset Confirm", "");

	}

	@Override
	protected void onSimulatorEnd() {
		removeBehaviour(releaseJobServer);
		removeBehaviour(blockRequestServer);
		removeBehaviour(unBlockquestServer);
		removeBehaviour(finishquestServer);
		removeBehaviour(globalDatarequestServer);
	}

	public Release(String name) {
		super(name);
		releaseJobServer = new ReleaseJobServer();
		blockRequestServer = new BlockRequestServer();
		unBlockquestServer = new UnBlockquestServer();
		finishquestServer = new FinishquestServer();
		globalDatarequestServer = new GlobalDatarequestServer();
		addBehaviour(releaseJobServer);
		addBehaviour(blockRequestServer);
		addBehaviour(unBlockquestServer);
		addBehaviour(finishquestServer);
		addBehaviour(globalDatarequestServer);
	}

	@SuppressWarnings("unchecked")
	public Release clone(AgentEnvironment environment,
			AgentBasedModel agentModel, ReleaseDataset releaseDataset,
			JobDataset jobDataset) {
		Release release = new Release(simulatorInfo.releaseAgentName);
		super.clone(release, environment);
		release.simulatorInfo = simulatorInfo;
		release.setProducts(products);
		release.random = new Random(100);
		release.maxReleaseBufferSize = maxReleaseBufferSize;
		release.releaseProbRanges = releaseProbRanges;
		release.releasePolicy = releasePolicy;
		release.agentModel = agentModel;
		release.releaseJobNum = releaseJobNum;
		release.releaseNumByType = releaseNumByType.clone();
		release.wip = wip;
		release.setWipDetail(getWipDetail().clone());
		release.wipReachConst = wipReachConst;
		release.wipReachConstDetail = wipReachConstDetail.clone();
		release.blockJob = (Hashtable<String, Integer>) blockJob.clone();
		release.outofMemory = outofMemory;
		release.releaseDataset = releaseDataset;
		release.setAdditionalString("Sub Simulation");
		release.setSubSimulation(null);

		release.jobDataset = jobDataset;
		for (Product p : products) {
			release.releaseDataset.addWIP(p.getIndex(), currentTime(),
					wipDetail[p.getIndex()], wip);
		}
		return release;
	}

	public Release simpleClone(Release release) {
		super.simpleClone(release);
		release.simulatorInfo = simulatorInfo;
		release.products = products;
		release.random = random;
		release.maxReleaseBufferSize = maxReleaseBufferSize;
		release.releaseProbRanges = releaseProbRanges;
		release.releasePolicy = releasePolicy;
		release.agentModel = agentModel;
		release.releaseJobNum = releaseJobNum;
		release.releaseNumByType = releaseNumByType;
		release.wip = wip;
		release.setWipDetail(getWipDetail());
		release.wipReachConst = wipReachConst;
		release.wipReachConstDetail = wipReachConstDetail;
		release.blockJob = blockJob;
		release.outofMemory = outofMemory;
		release.releaseDataset = releaseDataset;
		release.jobDataset = jobDataset;

		release.releaseJobServer = releaseJobServer;
		release.blockRequestServer = blockRequestServer;
		release.unBlockquestServer = unBlockquestServer;
		release.finishquestServer = finishquestServer;
		release.globalDatarequestServer = globalDatarequestServer;
		release.setCollectData(isCollectData());
		return release;
	}

	public void reset() {
		wip = 0;
		blockJob.clear();
		releaseJobNum = 0;
		for (int i = 0; i < releaseNumByType.length; i++) {
			releaseNumByType[i] = 0;
			wipReachConstDetail[i] = false;
			getWipDetail()[i] = 0;
			getProducts()[i].reset();
		}
		wipReachConst = false;
		super.resetAgent();
	}

	private void conwipControl(int jobTypeIndex) {
		if (releasePolicy.isPolicy(ReleasePolicy.ConstantWIP)) {
			if (simulatorInfo.simulateEndCondition == SimEndCondition.EndByPlanTime
					|| simulatorInfo.simulateEndCondition == SimEndCondition.EndByPlantLot
					&& releaseJobNum < simulatorInfo.planLotNum) {

				if (wip < releasePolicy.costWIP
						&& (blockJob.size() < maxReleaseBufferSize)
						&& wipReachConst) {

					Activation m = new Activation(currentTime(),
							getAgentName(), CustomActivation.JobRelease);
					sendSimulationMsg(m);
				}
			}

		} else if (releasePolicy.isPolicy(ReleasePolicy.ConstantWIPDetail)) {
			if (simulatorInfo.simulateEndCondition == SimEndCondition.EndByPlanTime
					|| simulatorInfo.simulateEndCondition == SimEndCondition.EndByPlantLot
					&& releaseJobNum < simulatorInfo.planLotNum) {
				if (getWipDetail()[jobTypeIndex] < getProducts()[jobTypeIndex]
						.getReleaseConstantWIP()
						&& (blockJob.size() < maxReleaseBufferSize)
						&& wipReachConstDetail[jobTypeIndex]) {

					Activation m = new Activation(currentTime(),
							getAgentName(), CustomActivation.JobRelease);
					m.setContent(String.valueOf(jobTypeIndex));
					sendSimulationMsg(m);
				}
			}
		} else if (releasePolicy.isPolicy(ReleasePolicy.IntelligentControl)) {
			if (simulatorInfo.simulateEndCondition == SimEndCondition.EndByPlanTime
					|| simulatorInfo.simulateEndCondition == SimEndCondition.EndByPlantLot
					&& releaseJobNum < simulatorInfo.planLotNum) {
				if ((blockJob.size() < maxReleaseBufferSize)
						&& wipReachConstDetail[jobTypeIndex]) {

					int releaseNum = getReleaseNumber(jobTypeIndex);
					for (int i = 0; i < releaseNum; i++) {
						Activation m = new Activation(currentTime(),
								getAgentName(), CustomActivation.JobRelease);
						m.setContent(String.valueOf(jobTypeIndex));
						sendSimulationMsg(m);
					}
				}
			}
		}
	}

	public void endConditionMeetOrNot() {
		if (simulatorInfo.simulateEndCondition == SimEndCondition.EndByPlantLot
				&& releaseJobNum >= simulatorInfo.planLotNum) {
			Activation m = new Activation(1, "a", "a");
			m.setType(2);
			sendSimulationMsg(m);
		}
	}

	private void createNewReleaseActivation(int jobTypeIndex) {
		String jobTypeIndexStr = String.valueOf(jobTypeIndex);
		if (simulatorInfo.simulateEndCondition == SimEndCondition.EndByPlantLot) {

			if (releasePolicy.isPolicy(ReleasePolicy.ConstantTime)) {
				if (releaseJobNum < simulatorInfo.planLotNum) {
					Activation m = new Activation(currentTime()
							+ (int) (releasePolicy.intervalTime * 60),
							getAgentName(), CustomActivation.JobRelease);

					sendSimulationMsg(m);
				} else {

					Activation m = new Activation();
					m.setType(2);
					sendSimulationMsg(m);
				}
			} else if (releasePolicy.isPolicy(ReleasePolicy.ConstantWIP)) {
				if (releaseJobNum > simulatorInfo.planLotNum) {
					Activation m = new Activation();
					m.setType(2);
					sendSimulationMsg(m);
				} else {
					if (!wipReachConst) {
						if (wip >= releasePolicy.costWIP) {
							wipReachConst = true;
							// sendSimulationMsg("Activation Reply");
						} else {

							Activation m = new Activation(currentTime()
									+ (int) (releasePolicy.intervalTime * 60),
									getAgentName(), CustomActivation.JobRelease);
							sendSimulationMsg(m);

						}
					}
				}// else {
					// sendSimulationMsg("Activation Reply");
				// }

			} else if (releasePolicy.isPolicy(ReleasePolicy.ConstantWIPDetail)) {
				if (releaseJobNum == 1) {
					for (int i = 0; i < getProducts().length; i++) {
						long interval = 0;
						if (i == 0)
							interval = (int) (releasePolicy.intervalTime * 60);
						Activation m = new Activation(interval, getAgentName(),
								CustomActivation.JobRelease, String.valueOf(i));
						sendSimulationMsg(m);
					}
				} else {
					if (releaseJobNum > simulatorInfo.planLotNum) {
						Activation m = new Activation(1, "a", "a");
						m.setType(2);
						sendSimulationMsg(m);
					} else {

						if (!wipReachConstDetail[jobTypeIndex]) {
							if (getWipDetail()[jobTypeIndex] >= getProducts()[jobTypeIndex]
									.getReleaseConstantWIP()) {
								wipReachConstDetail[jobTypeIndex] = true;
								// sendSimulationMsg("Activation Reply");
							} else {
								Activation m = new Activation(
										currentTime()
												+ (long) (getProducts()[jobTypeIndex]
														.getReleaseIntervalTime() * 60),
										getAgentName(),
										CustomActivation.JobRelease,
										jobTypeIndexStr);
								sendSimulationMsg(m);

							}
						}
					}
				}
			} else if (releasePolicy.isPolicy(ReleasePolicy.ConstantTimeDetail)) {
				if (releaseJobNum == 1) {
					for (int i = 0; i < getProducts().length; i++) {
						double interval = 0;
						if (i == 0)
							interval = getProducts()[jobTypeIndex]
									.getReleaseIntervalTimeR().getNext() * 60;
						Activation m = new Activation((long) interval,
								getAgentName(), CustomActivation.JobRelease,
								String.valueOf(i));
						sendSimulationMsg(m);
					}
				} else {
					if (releaseJobNum < simulatorInfo.planLotNum) {
						Activation m = new Activation(
								currentTime()
										+ (long) (getProducts()[jobTypeIndex]
												.getReleaseIntervalTimeR()
												.getNext() * 60),
								getAgentName(), CustomActivation.JobRelease,
								jobTypeIndexStr);
						sendSimulationMsg(m);
					} else {
						Activation m = new Activation();
						m.setType(2);
						sendSimulationMsg(m);
					}
				}
			}

		} else if (simulatorInfo.simulateEndCondition == SimEndCondition.EndByPlanTime) {

			if (releasePolicy.isPolicy(ReleasePolicy.ConstantTime)) {
				Activation m = new Activation(currentTime()
						+ (int) (releasePolicy.intervalTime * 60),
						getAgentName(), CustomActivation.JobRelease);

				sendSimulationMsg(m);
			} else if (releasePolicy.isPolicy(ReleasePolicy.ConstantWIP)) {
				if (!wipReachConst) {
					if (wip >= releasePolicy.costWIP) {
						wipReachConst = true;
						// sendSimulationMsg("Activation Reply");
					} else {
						Activation m = new Activation(currentTime()
								+ (int) (releasePolicy.intervalTime * 60),
								getAgentName(), CustomActivation.JobRelease);

						sendSimulationMsg(m);
					}
				}
			} else if (releasePolicy.isPolicy(ReleasePolicy.ConstantWIPDetail)
					|| releasePolicy.isPolicy(ReleasePolicy.IntelligentControl)) {
				if (releaseJobNum == 1) {
					for (int i = 0; i < getProducts().length; i++) {
						long interval = 0;
						if (i == 0)
							interval = (long) (getProducts()[jobTypeIndex]
									.getReleaseIntervalTimeR().getNext() * 60);
						Activation m = new Activation(interval, getAgentName(),
								CustomActivation.JobRelease, String.valueOf(i));
						sendSimulationMsg(m);
					}
				} else {
					if (!wipReachConstDetail[jobTypeIndex]) {
						if (getWipDetail()[jobTypeIndex] >= getProducts()[jobTypeIndex]
								.getReleaseConstantWIP()) {
							wipReachConstDetail[jobTypeIndex] = true;
						} else {
							Activation m = new Activation(currentTime()
									+ (long) (getProducts()[jobTypeIndex]
											.getReleaseIntervalTimeR()
											.getNext() * 60), getAgentName(),
									"Job Release", jobTypeIndexStr);
							sendSimulationMsg(m);
						}
					}
				}
			
			} else if (releasePolicy.isPolicy(ReleasePolicy.ConstantTimeDetail)) {
				if (releaseJobNum == 1) {
					for (int i = 0; i < getProducts().length; i++) {
						double interval = 0;
						if (i == 0)
							interval = getProducts()[jobTypeIndex]
									.getReleaseIntervalTimeR().getNext() * 60;
						Activation m = new Activation((long) interval,
								getAgentName(), CustomActivation.JobRelease,
								String.valueOf(i));
						sendSimulationMsg(m);
					}

				} else {

					Activation m = new Activation(currentTime()
							+ (long) (getProducts()[jobTypeIndex]
									.getReleaseIntervalTimeR().getNext() * 60),
							getAgentName(), "Job Release", jobTypeIndexStr);
					sendSimulationMsg(m);

				}

			}
		}
	}

	public ReleasePolicy getReleasePolicy() {
		return releasePolicy;
	}

	public void setReleasePolicy(ReleasePolicy releasePolicy) {
		this.releasePolicy = releasePolicy;
	}

	public int getReleaseJobNum() {
		return releaseJobNum;
	}

	public void setReleaseJobNum(int releaseJobNum) {
		this.releaseJobNum = releaseJobNum;
	}

	public int[] getWipDetail() {
		return wipDetail;
	}

	public void setWipDetail(int[] wipDetail) {
		this.wipDetail = wipDetail;
	}

	public Product[] getProducts() {
		return products;
	}

	public void setProducts(Product[] products) {
		this.products = products;
	}

	public SimulationInfo getSimulatorInfo() {
		return simulatorInfo;
	}

	public void setSimulatorInfo(SimulationInfo simulatorInfo) {
		this.simulatorInfo = simulatorInfo;
	}

	public int a(int jobTypeIndex) {

		int releaseNum = 0;
		if (getSubSimulation() != null) {
			synchronized (getSubSimulation().isRunning()) {
				if (!getSubSimulation().isRunning()) {
					getSubSimulation().setRunning(true);
					getSubSimulation().pauseMainSimulation();
					getSubSimulation().setHistoryDecisions();
					int maxReleaseNumber = products[jobTypeIndex].getMaxWIP()
							- wipDetail[jobTypeIndex];
					maxReleaseNumber=2;
					String alternatives = "";

					DataMeta<Integer> data = new DataMeta<Integer>(
							getSubSimulation().cloneAgentModel());
					getSubSimulation().getTrainingDataset().addReleaseData(
							products[jobTypeIndex], data);

					for (int i = 0; i < maxReleaseNumber; i++) {
						alternatives += i + ",";
					}
					System.out.println(getAdditionalString() + ","
							+ getAgentName()
							+ " is making decisions, alternatives:"
							+ alternatives + "  ;time:" + currentTime()
							+ " ,last message:"
							+ this.getLastReceivedMessage().toString());

					ArrayList<Integer> bestNumber = new ArrayList<Integer>();
					double min = Double.MAX_VALUE;
					for (int i = 0; i < maxReleaseNumber; i++) {

						getSubSimulation().clone().runR(Release.this, i,
								jobTypeIndex);
						Message msg2 = blockingReceive("Subsimulation Done");
						System.out.println(msg2.getObjectContent().toString());

						data.addActionAndValue(i,
								(Double) msg2.getObjectContent());

						if (min > (Double) msg2.getObjectContent()) {
							min = (Double) msg2.getObjectContent();
							bestNumber.clear();
							bestNumber.add(i);
						} else if (min == (Double) msg2.getObjectContent()) {
							bestNumber.add(i);
						}
					}
					if (bestNumber.size() == 0) {
						System.out
								.println("=================================Error=======================");
					} else {
						releaseNum = bestNumber.get(bestNumber.size() - 1);
					}
					System.out.println(getAgentName()
							+ " decision is made, release number :"
							+ releaseNum + ", type:"
							+ products[jobTypeIndex].getName()
							+ ", //////wip: " + wipDetail[jobTypeIndex]);

					getHistoryDecisions().add(releaseNum);
					getSubSimulation().setRunning(false);
					getSubSimulation().continueMainSimulation();
				} else {
					int num = getProducts()[jobTypeIndex]
							.getReleaseConstantWIP()
							- getWipDetail()[jobTypeIndex];

					releaseNum = Math.max(0, num);
					getHistoryDecisions().add(releaseNum);
				}
			}

		} else if (getSubSimulation() == null
				&& getHistoryDecisionIndex() < getHistoryDecisions().size()) {
			releaseNum = (Integer) getHistoryDecisions().get(
					getHistoryDecisionIndex());
			setHistoryDecisionIndex(getHistoryDecisionIndex() + 1);
			System.out
					.println(getAdditionalString()
							+ "  ,"
							+ getAgentName()
							+ ",Select release number by history decsion.  selected number:"
							+ releaseNum);
		} else if (getSubSimulation() == null) {
			if (getDecisionAgent().equals(getAgentName())
					&& !isHasMadeDecision() && getDecision() != null) {
				releaseNum = (Integer) getDecision();
				setHasMadeDecision(true);

				System.out.println(getAdditionalString()
						+ ", selected release number:" + releaseNum);
			} else {
//				int num = getProducts()[jobTypeIndex].getReleaseConstantWIP()
//						- getWipDetail()[jobTypeIndex];
//
//				releaseNum = Math.max(0, num);
				
				long intva=(long) (getProducts()[jobTypeIndex].getReleaseIntervalTimeR().getParameter("mean")*60);
//				if(intva!=30*60){
//					intva=50*20;
//				}
				
				
				if(currentTime()/intva%5==0||currentTime()/intva%5==2){
				//if(currentTime()/intva%3==0){
				//if(currentTime()/intva%5==0){
					releaseNum=1;
				}else{
					releaseNum=0;
				}
			}
		}
		return releaseNum;
	}

	public int b(int jobTypeIndex) {
		int releaseNum = 0;
		if (getSubSimulation() != null) {

			int num = getProducts()[jobTypeIndex].getReleaseConstantWIP()
					- getWipDetail()[jobTypeIndex];

			releaseNum = Math.max(0, num);

			getHistoryDecisions().add(releaseNum);

		} else if (getSubSimulation() == null
				&& getHistoryDecisionIndex() < getHistoryDecisions().size()) {
			releaseNum = (Integer) getHistoryDecisions().get(
					getHistoryDecisionIndex());
			setHistoryDecisionIndex(getHistoryDecisionIndex() + 1);
			System.out
					.println(getAdditionalString()
							+ "  ,"
							+ getAgentName()
							+ ",Select release number by history decsion for job. selected number:"
							+ releaseNum);
		} else if (getSubSimulation() == null) {

			int num = getProducts()[jobTypeIndex].getReleaseConstantWIP()
					- getWipDetail()[jobTypeIndex];

			releaseNum = Math.max(0, num);

		}
		return releaseNum;
	}

	private int c(int jobTypeIndex) {

		int num = getProducts()[jobTypeIndex].getReleaseConstantWIP()
				- getWipDetail()[jobTypeIndex];

		return Math.max(0, num);
	}

	private int getReleaseNumber(int jobTypeIndex) {
		int releaseNum = a(jobTypeIndex);
		if (releaseNum == 0 && wipDetail[jobTypeIndex] == 0) {
			releaseNum = 1;
		}
		return releaseNum;
	}

}
