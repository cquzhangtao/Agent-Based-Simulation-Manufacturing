package manu.agent;

import java.util.ArrayList;

import manu.agent.component.Job;
import manu.agent.component.Release;
import manu.agent.component.ToolGroup;
import manu.agent.component.ToolGroupPool;
import manu.model.ManufactureModel;
import manu.model.component.JobInfo;
import manu.model.component.ToolGroupInfo;
import manu.model.component.ToolGroupPoolInfo;
import manu.scheduling.SubSimulation;
import manu.scheduling.training.data.FlowControlTrainingDataset;
import manu.simulation.info.SimulationInfo;
import manu.simulation.result.SimulationResult;
import manu.utli.Entity;
import simulation.framework.AgentEnvironment;
import simulation.framework.SimulationBase;

public class AgentBasedModel extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentEnvironment environment = new AgentEnvironment();
	private Release releaseAgent;
	private ArrayList<ToolGroup> toolGroupAgents = new ArrayList<ToolGroup>();
	private ArrayList<Job> jobAgents = new ArrayList<Job>();
	private ArrayList<ToolGroupPool> toolGroupPoolAgents = new ArrayList<ToolGroupPool>();
	private long currentTime = 0;

	public AgentBasedModel() {

	}

	public void generateModel(ManufactureModel manuModel, SimulationInfo simulatorInfo, SimulationResult results, SubSimulation subsimulation) {
		setReleaseAgent(new Release(simulatorInfo.releaseAgentName, new Object[] { simulatorInfo, manuModel, results.releaseDataset, results.jobDataset, this,
				subsimulation }));
		getEnvironment().register(getReleaseAgent());
		getReleaseAgent().start();
		for (int i = 0; i < manuModel.getToolGroups().size(); i++) {
			ToolGroupInfo toolGroupInfo = manuModel.getToolGroups().get(i);

			ToolGroup tg = new ToolGroup(toolGroupInfo.getToolGroupName(), new Object[] { toolGroupInfo,
					results.getBufferData(toolGroupInfo.getToolGroupName()), results.getToolGroupData(toolGroupInfo.getToolGroupName()),
					getReleaseAgent().getAgentName(), subsimulation });
			getEnvironment().register(tg);
			tg.start();
			toolGroupAgents.add(tg);
		}
		for (String skill : manuModel.getToolGroupBySkill().keySet()) {
			ToolGroupPoolInfo toolGroupsInfo = manuModel.getToolGroupBySkill().get(skill);
			ToolGroupPool tg = new ToolGroupPool(skill, toolGroupsInfo, subsimulation);
			getEnvironment().register(tg);

			tg.start();
			toolGroupPoolAgents.add(tg);

		}
		subsimulation.setTrainingDataset(new FlowControlTrainingDataset(getReleaseAgent().getProducts(), getToolGroupPoolAgents(), getToolGroupAgents()));

	}

	public ToolGroup getGoolGroupAgent(String name) {
		for (ToolGroup tg : toolGroupAgents) {
			if (tg.getAgentName().equals(name)) {
				return tg;
			}
		}
		return null;
	}

	public void start() {
		getReleaseAgent().start();
		for (ToolGroup tg : toolGroupAgents) {
			tg.start();
		}

		for (ToolGroupPool tg : toolGroupPoolAgents) {
			tg.start();
		}

		for (int i = 0; i < jobAgents.size(); i++) {
			try {
				jobAgents.get(i).start1();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();

			}
		}
	}

	public void destroy() {

		getReleaseAgent().destroyAgent();
		getReleaseAgent().notifyAgent();
		for (ToolGroup tg : toolGroupAgents) {
			tg.destroyAgent();
			tg.notifyAgent();
		}

		for (ToolGroupPool tg : toolGroupPoolAgents) {
			tg.destroyAgent();
			tg.notifyAgent();

		}
		for (Job job : getJobAgents()) {
			job.destroyAgent();
			job.notifyAgent();
		}
	}

	public void pauseAgents() {

		getReleaseAgent().pauseAgent();
		for (ToolGroup tg : toolGroupAgents) {
			tg.pauseAgent();
		}

		for (ToolGroupPool tg : toolGroupPoolAgents) {
			tg.pauseAgent();

		}
		synchronized (getJobAgents()) {
			for (int i = 0; i < getJobAgents().size(); i++) {

				getJobAgents().get(i).pauseAgent();
			}

		}

	}

	public void continueAgents() {

		getReleaseAgent().continueAgent();
		for (ToolGroup tg : toolGroupAgents) {
			tg.continueAgent();
		}

		for (ToolGroupPool tg : toolGroupPoolAgents) {
			tg.continueAgent();

		}
		synchronized (getJobAgents()) {
			for (int i = 0; i < getJobAgents().size(); i++) {

				getJobAgents().get(i).continueAgent();
			}

		}

	}

	public AgentBasedModel clone(SimulationResult results, String string) {

		// if (!check()) {
		// System.out.println("Model error, " + string);
		// System.out.println(environment.getAgentNames());
		// } else {
		// System.out.println("Model checked, " + string);
		// }
		AgentBasedModel newModel = new AgentBasedModel();
		newModel.currentTime = currentTime;

		newModel.setEnvironment(getEnvironment().clone());
		newModel.setReleaseAgent(getReleaseAgent().clone(newModel.getEnvironment(), newModel, results.getReleaseDataset(), results.getJobDataset()));
		synchronized (getJobAgents()) {
			for (Job job : getJobAgents()) {
				newModel.getJobAgents().add(job.clone(newModel.getEnvironment(), results.getJobDataset()));
			}
		}
		for (ToolGroup tg : toolGroupAgents) {
			newModel.toolGroupAgents.add(tg.clone(newModel, newModel.getEnvironment(), results.getToolGroupDataset(), results.bufferDataset));
		}
		for (ToolGroupPool tg : toolGroupPoolAgents) {
			newModel.toolGroupPoolAgents.add(tg.clone(newModel, newModel.getEnvironment()));
		}

		return newModel;

	}

	public boolean check() {
		for (ToolGroup tg : toolGroupAgents) {

			for (JobInfo jobInfo : tg.getBufferInfo().getJobsinBuffer()) {
				if (this.getJobAgent(jobInfo.getName()) == null) {
					return false;
				}
			}
		}
		return true;
	}

	public void removeJobAgent(String job) {
		synchronized (getJobAgents()) {
			for (Job j : getJobAgents()) {
				if (j.getAgentName().equals(job)) {
					getJobAgents().remove(j);
					return;
				}

			}
		}

	}

	public void addJobAgent(Job job) {
		synchronized (getJobAgents()) {
			getJobAgents().add(job);
		}
	}

	public Job getJobAgent(int index) {
		return getJobAgents().get(index);
	}

	public Job getJobAgent(String name) {
		for (Job j : getJobAgents()) {
			if (j.getAgentName().equals(name))
				return j;
		}
		return null;
	}

	public String getJobAgentNameString() {
		String str = "";
		for (Job j : getJobAgents()) {
			str += "," + j.getAgentName();

		}
		return str;

	}

	public ArrayList<String> getJobAgentNames() {
		ArrayList<String> op = new ArrayList<String>();
		for (Job j : getJobAgents()) {
			op.add(j.getAgentName());
		}
		return op;
	}

	public void reset() {
		getReleaseAgent().reset();
		for (ToolGroup t : toolGroupAgents) {
			t.reset();
		}
		getEnvironment().reset(getJobAgents());
		getJobAgents().clear();
	}

	public void close() {
		getReleaseAgent().destroyAgent();
		getReleaseAgent().notifyAgent();
		for (Job j : getJobAgents()) {
			j.destroyAgent();
			j.notifyAgent();
			j = null;
		}
		getJobAgents().clear();
		for (ToolGroup t : toolGroupAgents) {
			t.destroyAgent();
			t.notifyAgent();
			t = null;
		}
		toolGroupAgents.clear();
		getEnvironment().clear();

	}

	public void register(SimulationBase simulator) {
		getEnvironment().register(simulator);
	}

	public ToolGroup getToolGroupAgent(String name) {
		for (ToolGroup tg : toolGroupAgents) {
			if (tg.getAgentName().equals(name))
				return tg;
		}
		return null;
	}

	public ToolGroupPool getToolGroupPoolAgent(String name) {
		for (ToolGroupPool tg : toolGroupPoolAgents) {
			if (tg.getAgentName().equals(name))
				return tg;
		}
		return null;
	}

	public ArrayList<ToolGroup> getToolGroupAgents() {
		return toolGroupAgents;
	}

	public ArrayList<Job> getJobAgents() {

		return jobAgents;

	}

	public void setJobAgents(ArrayList<Job> jobAgents) {
		this.jobAgents = jobAgents;
	}

	public Release getReleaseAgent() {
		return releaseAgent;
	}

	public void setReleaseAgent(Release releaseAgent) {
		this.releaseAgent = releaseAgent;
	}

	public AgentEnvironment getEnvironment() {
		return environment;
	}

	public void setEnvironment(AgentEnvironment environment) {
		this.environment = environment;
	}

	public void setDecision(String decisionAgent, Object decision) {
		getReleaseAgent().setDecisionAgent(decisionAgent);
		getReleaseAgent().setDecision(decision);
		for (ToolGroup tg : toolGroupAgents) {
			tg.setDecisionAgent(decisionAgent);
			tg.setDecision(decision);
		}

		for (ToolGroupPool tg : toolGroupPoolAgents) {
			tg.setDecisionAgent(decisionAgent);
			tg.setDecision(decision);
		}

	}

	public void initHistoryDecisions() {
		getReleaseAgent().setHistoryDecisionIndex(0);
		getReleaseAgent().getHistoryDecisions().clear();
		for (ToolGroup tg : toolGroupAgents) {
			tg.setHistoryDecisionIndex(0);
			tg.getHistoryDecisions().clear();
		}

		for (ToolGroupPool tg : toolGroupPoolAgents) {
			tg.setHistoryDecisionIndex(0);
			tg.getHistoryDecisions().clear();
		}

	}

	public void setHistoryDecisions(AgentBasedModel agentModel) {
		getReleaseAgent().setHistoryDecisionIndex(0);
		getReleaseAgent().setHistoryDecisions(new ArrayList<Object>(agentModel.getReleaseAgent().getHistoryDecisions()));
		for (ToolGroup tg : toolGroupAgents) {
			tg.setHistoryDecisionIndex(0);
			tg.setHistoryDecisions(new ArrayList<Object>(agentModel.getToolGroupAgent(tg.getAgentName()).getHistoryDecisions()));
		}

		for (ToolGroupPool tg : toolGroupPoolAgents) {
			tg.setHistoryDecisionIndex(0);
			tg.setHistoryDecisions(new ArrayList<Object>(agentModel.getToolGroupPoolAgent(tg.getAgentName()).getHistoryDecisions()));
		}
	}

	public ArrayList<ToolGroupPool> getToolGroupPoolAgents() {
		return toolGroupPoolAgents;
	}

	public void setToolGroupPoolAgents(ArrayList<ToolGroupPool> toolGroupPoolAgents) {
		this.toolGroupPoolAgents = toolGroupPoolAgents;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
}
