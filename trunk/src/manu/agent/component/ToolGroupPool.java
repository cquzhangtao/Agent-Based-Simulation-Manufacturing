package manu.agent.component;

import java.util.ArrayList;
import java.util.Random;

import manu.agent.AgentBasedModel;
import manu.model.component.AllocationRule;
import manu.model.component.JobInfo;
import manu.model.component.ToolGroupInfo;
import manu.model.component.ToolGroupPoolInfo;
import manu.scheduling.SubSimulation;
import manu.scheduling.training.data.DataMeta;
import simulation.framework.AgentEnvironment;
import simulation.framework.Behaviour;
import simulation.framework.Message;

public class ToolGroupPool extends SimAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ToolGroupPoolInfo toolGroupPoolInfo;
	private Random rnd=new Random();
	

	public ToolGroupPool(String agentName,
			ToolGroupPoolInfo toolGroupsInfo, SubSimulation subSimulation) {
		super(agentName);
		this.setAdditionalString("Main Simulation");
		this.toolGroupPoolInfo = toolGroupsInfo;
		super.setSubSimulation(subSimulation);
		this.addBehaviour(new SelectToolGroupServer());
	}

	public ToolGroupPool(String name) {
		super(name);
		this.toolGroupPoolInfo = new ToolGroupPoolInfo();
		this.addBehaviour(new SelectToolGroupServer());
	}

	public void simpleClone(ToolGroupPool tSkill) {
		super.simpleClone(tSkill);
		tSkill.toolGroupPoolInfo = toolGroupPoolInfo;
	}
	
	public ToolGroupInfo a(JobInfo jobInfo){
		ToolGroupInfo bestTG = null;
		if (getSubSimulation() != null) {
			synchronized(getSubSimulation().isRunning()){
			if (!getSubSimulation().isRunning()
					&& toolGroupPoolInfo.getToolGroups().size() > 1
					&& getDecisionAgent().isEmpty()) {
				getSubSimulation().setRunning(true);
				System.out.println(getAgentName()
						+ " is making decisions, alternatives: "
						+ toolGroupPoolInfo.getToolGroupNames());
				getSubSimulation().setRunning(true);
				getSubSimulation().pauseMainSimulation();
				getSubSimulation().setHistoryDecisions();
				ArrayList<ToolGroupInfo> bestToolGroup = new ArrayList<ToolGroupInfo>();
				double min = Double.MAX_VALUE;
				AgentBasedModel clonedAgentModel = null;
				DataMeta<ToolGroupInfo> data=null;
				if(getSubSimulation().isCollectData()){
				clonedAgentModel = getSubSimulation().cloneAgentModel();
				data=new DataMeta<ToolGroupInfo> (clonedAgentModel);
				data.setLocation(clonedAgentModel.getJobAgent(jobInfo.getName()).getJobInfo());
				getSubSimulation().getTrainingDataset().addRoutingData(getAgentName(), data);
				}
	
				for (int i =0;i< toolGroupPoolInfo.getToolGroups().size() ; i++) {
					ToolGroupInfo tgi=toolGroupPoolInfo.getToolGroups().get(i);
					getSubSimulation().clone().run(
							ToolGroupPool.this,
							tgi);
					Message msg2 = blockingReceive("Subsimulation Done");
					System.out.println(msg2.getObjectContent()
							.toString());
					if(getSubSimulation().isCollectData()){
					data.addActionAndValue(clonedAgentModel.getToolGroupAgent(tgi.getToolGroupName()).getToolGroupInfo(), (Double) msg2.getObjectContent());
					}
					if (min >(Double) msg2.getObjectContent()) {
						min = (Double) msg2.getObjectContent();
						bestToolGroup.clear();
						bestToolGroup.add(toolGroupPoolInfo
								.getToolGroups().get(i));
					}  else if (min==(Double) msg2.getObjectContent()) {
						bestToolGroup.add(toolGroupPoolInfo
								.getToolGroups().get(i));
					}
				}
				if (bestToolGroup.size() == 0) {
					System.out
							.println("=================================Error=======================");
				} else {
					bestTG =toolGroupPoolInfo
							.getBestToolGroup(bestToolGroup,
									jobInfo,
									currentTime()); 
				}
				System.out.println(getAgentName()
						+ " decision is made, selected tool group:"
						+ (bestTG == null ? "" : bestTG
								.getToolGroupName()));
				getHistoryDecisions().add(bestTG);
				getSubSimulation().setRunning(false);
				getSubSimulation().continueMainSimulation();

			} else {
				bestTG = toolGroupPoolInfo
						.getBestToolGroup(
								jobInfo,
								currentTime());
//				System.out.println(getAdditionalString() + "  ,"
//						+ getName() + ",Select Tool by rules for job "
//						+ msg.getSender() + ", selected tool:"
//						+ bestTG.getToolGroupName());
				getHistoryDecisions().add(bestTG);
			}
			}
			

		} else if (getSubSimulation() == null
				&& getHistoryDecisionIndex() < getHistoryDecisions()
						.size()) {
			bestTG = (ToolGroupInfo) getHistoryDecisions().get(
					getHistoryDecisionIndex());
			setHistoryDecisionIndex(getHistoryDecisionIndex() + 1);
			System.out.println(getAdditionalString() + "  ,"
					+ getAgentName()
					+ ",Select Tool by history decsion for job "
					+ jobInfo.getName() + ", selected tool:"
					+ bestTG.getToolGroupName());
		} else if (getSubSimulation() == null) {
			if (getDecisionAgent().equals(getAgentName())
					&& !isHasMadeDecision() && getDecision() != null) {
				bestTG = (ToolGroupInfo) getDecision();
				setHasMadeDecision(true);

				System.out.println(getAdditionalString()
						+ ", selected tool group:"
						+ bestTG.getToolGroupName());

			} else {

				bestTG = toolGroupPoolInfo
						.getBestToolGroup(
								jobInfo,
								currentTime(),AllocationRule.SQT);
//				System.out.println(getAdditionalString() + "  ,"
//						+ getName() + ",Select Tool by rules for job "
//						+ msg.getSender() + ", selected tool:"
//						+ bestTG.getToolGroupName());

			}
		}
		return bestTG;
	}
	
	public ToolGroupInfo b(JobInfo jobInfo){
		ToolGroupInfo bestTG = null;
		if (getSubSimulation() != null) {
			
				bestTG = toolGroupPoolInfo
						.getBestToolGroup(
								jobInfo,
								currentTime());
//				System.out.println(getAdditionalString() + "  ,"
//						+ getName() + ",Select Tool by rules for job "
//						+ msg.getSender() + ", selected tool:"
//						+ bestTG.getToolGroupName());
			
			getHistoryDecisions().add(bestTG);

		} else if (getSubSimulation() == null
				&& getHistoryDecisionIndex() < getHistoryDecisions()
						.size()) {
			bestTG = (ToolGroupInfo) getHistoryDecisions().get(
					getHistoryDecisionIndex());
			setHistoryDecisionIndex(getHistoryDecisionIndex() + 1);
			System.out.println(getAdditionalString() + "  ,"
					+ getAgentName()
					+ ",Select Tool by history decsion for job "
					+ jobInfo.getName() + ", selected tool:"
					+ bestTG.getToolGroupName());
		} else if (getSubSimulation() == null) {


				bestTG = toolGroupPoolInfo
						.getBestToolGroup(
								jobInfo,
								currentTime());
//				System.out.println(getAdditionalString() + "  ,"
//						+ getName() + ",Select Tool by rules for job "
//						+ msg.getSender() + ", selected tool:"
//						+ bestTG.getToolGroupName());

	
		}
		return bestTG;
	}
	private ToolGroupInfo c(JobInfo jobInfo){
		ToolGroupInfo bestTG = null;
				bestTG = toolGroupPoolInfo
						.getBestToolGroup(
								jobInfo,
								currentTime(),AllocationRule.SQT);

		return bestTG;
	}

	private class SelectToolGroupServer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			Message msg = receive("Select Tool Group");
			if (msg != null) {
				
				ToolGroupInfo bestTG = null;

				bestTG=c((JobInfo) msg.getObjectContent());
				

				Message reply = msg.createReply();
				reply.setContent(bestTG.getToolGroupName());
				send(reply);

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

	}

	@Override
	protected void onSimulatorEnd() {
		// TODO Auto-generated method stub

	}

	public ToolGroupPool clone(AgentBasedModel agentModel,
			AgentEnvironment environment) {
		ToolGroupPool tgs = new ToolGroupPool(
				this.getAgentName());
		super.clone(tgs, environment);
		tgs.rnd=rnd;
		tgs.setAdditionalString("Sub Simulation");
		tgs.toolGroupPoolInfo.setSkill(this.toolGroupPoolInfo.getSkill());
		for (ToolGroupInfo tgi : this.toolGroupPoolInfo.getToolGroups()) {
			tgs.toolGroupPoolInfo.getToolGroups().add(
					agentModel.getGoolGroupAgent(tgi.getToolGroupName())
							.getToolGroupInfo());
		}
		return tgs;
	}

	public ToolGroupPoolInfo getToolGroupsInfo() {
		return toolGroupPoolInfo;
	}

	public void setToolGroupsInfo(ToolGroupPoolInfo toolGroupsInfo) {
		this.toolGroupPoolInfo = toolGroupsInfo;
	}

}
