package manu.agent.component;

import java.util.ArrayList;
import java.util.List;

import manu.scheduling.SubSimulation;
import simulation.framework.Agent;
import simulation.framework.AgentEnvironment;
import simulation.framework.Behaviour;
import simulation.framework.Message;


public abstract class SimAgent extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isForceDestroied=false;
	private boolean collectData = true;
	private String decisionAgent="";
	private Object decision="";
	private boolean hasMadeDecision=false;
	private SubSimulation subSimulation;
	private List<Object>historyDecisions=new ArrayList<Object>();
	private int historyDecisionIndex=0;
	
	protected SimAgent(String agentName) {
		super(agentName);
		addBehaviour(new SimulationStartServer());
		addBehaviour(new SimulationResetServer());
		addBehaviour(new SimulationEndServer());
	}
	
	public void simpleClone(SimAgent agent){
		super.simpleClone(agent);
		agent.isForceDestroied=isForceDestroied;
		agent.subSimulation=subSimulation;
	}
	
	// public String info;
	protected abstract void onSimulatorStart();

	protected abstract void onSimulatorReset();

	protected abstract void onSimulatorEnd();

	private class SimulationStartServer extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			// TODO Auto-generated method stub
			Message msg = receive("Simulate Start");
			if (msg != null) {
				onSimulatorStart();
				// Logging Flag// if(simulatorInfo.logging)
				// Logging Flag// writeLog("Simulator start");
			}
			return 1;
		}
	}

	private class SimulationResetServer extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			// TODO Auto-generated method stub
			Message msg = receive("Simulate Reset");
			if (msg != null) {
				onSimulatorReset();
				SimAgent.this.resetAgent();
			}
			return 1;
		}
	}

	class SimulationEndServer extends Behaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int action() {
			// TODO Auto-generated method stub
			Message msg = receive("Simulate End");
			if (msg != null) {
				onSimulatorEnd();
				if (msg.getContent().equals("Destroy")) {
					setForceDestroied(true);
					destroyAgent();
					
					return 0;
				}

			}
			return 1;
		}
	}
	@Override
	public void afterAgentDestroy() {
		// TODO Auto-generated method stub
		if(isForceDestroied())
			send("Simulator","Destroy Confirm","");
	}
	
	
	public void clone(SimAgent agent,AgentEnvironment environment){
		super.clone(agent, environment);
		agent.setForceDestroied(isForceDestroied);
		agent.historyDecisions=new ArrayList<Object>(historyDecisions);
		agent.historyDecisionIndex=historyDecisionIndex;
	}

	public String getDecisionAgent() {
		return decisionAgent;
	}

	public void setDecisionAgent(String decisionAgent) {
		this.decisionAgent = decisionAgent;
	}

	public Object getDecision() {
		return decision;
	}

	public void setDecision(Object decision) {
		this.decision = decision;
	}

	public boolean isForceDestroied() {
		return isForceDestroied;
	}

	public void setForceDestroied(boolean isForceDestroied) {
		this.isForceDestroied = isForceDestroied;
	}

	public boolean isHasMadeDecision() {
		return hasMadeDecision;
	}

	public void setHasMadeDecision(boolean hasMadeDecision) {
		this.hasMadeDecision = hasMadeDecision;
	}

	public SubSimulation getSubSimulation() {
		return subSimulation;
	}

	public void setSubSimulation(SubSimulation subSimulation) {
		this.subSimulation = subSimulation;
	}

	public int getHistoryDecisionIndex() {
		return historyDecisionIndex;
	}

	public void setHistoryDecisionIndex(int historyDecisionIndex) {
		this.historyDecisionIndex = historyDecisionIndex;
	}

	public List<Object> getHistoryDecisions() {
		return historyDecisions;
	}

	public void setHistoryDecisions(List<Object> historyDecisions) {
		this.historyDecisions = historyDecisions;
	}

	public boolean isCollectData() {
		return collectData;
	}

	public void setCollectData(boolean collectData) {
		this.collectData = collectData;
	}
}
