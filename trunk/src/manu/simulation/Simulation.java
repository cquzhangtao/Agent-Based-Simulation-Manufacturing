package manu.simulation;

import java.util.ArrayList;
import java.util.List;

import manu.model.ManufactureModel;
import manu.model.component.ToolGroupInfo;
import manu.model.component.ToolInfo;
import manu.model.feature.Interrupt;
import manu.scheduling.SubSimulation;
import manu.simulation.others.CommonMethods;
import simulation.framework.Activation;
import simulation.framework.AgentEnvironment;
import simulation.framework.Message;

public class Simulation extends simulation.framework.Simulation {

	private String release;
	//private Release releaseAgent;
	//public SampleCollector collector;
	//private boolean collectData = false;
	private List<SimulationListener> simulationListener = new ArrayList<SimulationListener>();
	private SubSimulation subSimulation;

	public Simulation() {
		super(new CustomActivation());
		release = "Release";
		setAdditionalString("Main Simulation");
	}

	public void addSimulationListener(SimulationListener sl) {
		simulationListener.add(sl);
	}

	public void loadToolBreakdownEvent(ManufactureModel manuModel) {

		for (ToolGroupInfo toolGroupInfo : manuModel.getToolGroups()) {
			for (Interrupt inter : toolGroupInfo.getInterrupts()) {
				if (inter.applyTo.equals("OneTool")) {
					Activation activation = new Activation(
							inter.nextOccurAfterTime(),
							toolGroupInfo.getToolGroupName(),
							CustomActivation.Interrupt, "OneTool%"
									+ inter.index);
					getEventList().addActivateEvent(activation);
				}
			}

			for (ToolInfo toolInfo : toolGroupInfo.getToolInfos()) {
				for (Interrupt inter : toolInfo.getInterrupts()) {

					Activation activation = new Activation(
							inter.nextOccurAfterTime(),
							toolGroupInfo.getToolGroupName(),
							CustomActivation.Interrupt, "SingleTool%"
									+ toolInfo.toolIndex + "%" + inter.index);
					getEventList().addActivateEvent(activation);

				}
			}

		}

		// for (int i = 0; i < manuModel.toolGroups.size(); i++) {
		// ToolGroupInfo toolGroupInfo = manuModel.toolGroups.get(i);
		// Interrupt[] eachInterrupt = toolGroupInfo.getInterruptinEachTool();
		// Interrupt[] oneInterrupt = toolGroupInfo.getInterruptinOneTool();
		// Interrupt[] specifyInterrupt =
		// toolGroupInfo.getInterruptinSpecifiedTool();
		//
		// if (eachInterrupt != null)
		// for (int j = 0; j < toolGroupInfo.toolNuminGroup; j++)
		// for (int k = 0; k < eachInterrupt.length; k++) {
		// Activation activation = new Activation(
		// eachInterrupt[k].nextOccurAfterTime(),
		// toolGroupInfo.toolGroupName,
		// CustomActivation.Interrupt, "EachTool%"
		// + eachInterrupt[k].index + "%" + j);
		// eventList.addActivateEvent(activation);
		// }
		// if (oneInterrupt != null)
		// for (int k = 0; k < oneInterrupt.length; k++) {
		// Activation activation = new Activation(
		// oneInterrupt[k].nextOccurAfterTime(),
		// toolGroupInfo.toolGroupName,
		// CustomActivation.Interrupt, "OneTool%"
		// + oneInterrupt[k].index);
		// eventList.addActivateEvent(activation);
		// }
		// if (specifyInterrupt != null)
		// for (int k = 0; k < specifyInterrupt.length; k++) {
		// Activation activation = new Activation(
		// specifyInterrupt[k].nextOccurAfterTime(),
		// toolGroupInfo.toolGroupName,
		// CustomActivation.Interrupt, "SpecifyTool%"
		// + specifyInterrupt[k].index+ "%" +
		// specifyInterrupt[k].specifiedToolIndex);
		// eventList.addActivateEvent(activation);
		// }
		//
		// }

	}

	@Override
	public void onSimStart() {
		getEventList().addActivateEvent(new Activation(0, release,
				CustomActivation.JobRelease));
		CommonMethods.isleep(100);
	}
	
	public void addActivation(Activation act){
		getEventList().addActivateEvent(act);
	}

	public void closeModel(ArrayList<ToolGroupInfo> tools) {

		send(release, "Simulate End", "Destroy");
		Message msg = receive("Destroy Confirm");
		while (msg == null || !msg.getSender().equals(release)) {
			msg = receive("Destroy Confirm");
		}
		for (int i = 0; i < tools.size(); i++) {
			send(tools.get(i).getToolGroupName(), "Simulate End", "Destroy");
			Message msg1 = receive("Destroy Confirm");
			while (msg1 == null
					|| !msg1.getSender().equals(tools.get(i).getToolGroupName())) {
				msg1 = receive("Destroy Confirm");
			}
		}
		super.resetSimulation();
		//releaseAgent = null;
		synchronized (getEnvironment().getMessageQueues()) {
			getEnvironment().clearAgent();
			setMessageQueue(getEnvironment().addSimulator());

		}

	}

	public void closeModel() {
		super.resetSimulation();
		//releaseAgent = null;
		getEnvironment().register(this);
	}

	@Override
	public void onSimEnd() {
		//2014
		//collector.saveToXls();
		//
		for (SimulationListener sl : simulationListener) {
			sl.onEnd();
		}
	}

	@Override
	public void onSimAdvance() {
		// TODO Auto-generated method stub
		for (SimulationListener sl : simulationListener) {
			sl.onTimeAdvance();
		}

	}

	public void resetSimulation1(ManufactureModel manuModel) {
		// TODO Auto-generated method stub
		ArrayList<ToolGroupInfo> tools = manuModel.getToolGroups();
		for (int i = 0; i < tools.size(); i++) {
			send(tools.get(i).getToolGroupName(), "Simulate Reset", "");
			Message msg = receive("Reset Confirm");
			while (msg == null
					|| !msg.getSender().equals(tools.get(i).getToolGroupName())) {
				msg = receive("Reset Confirm");
			}
		}
		send(release, "Simulate Reset", "");
		Message msg = receive("Reset Confirm");
		while (msg == null || !msg.getSender().equals(release)) {
			msg = receive("Reset Confirm");
		}
		super.resetSimulation();
		loadToolBreakdownEvent(manuModel);
		CommonMethods.isleep(500);
	}

	public void reset(ManufactureModel manuModel) {
		super.resetSimulation();
		//loadToolBreakdownEvent(manuModel);
	}

	@Override
	public boolean endCondition() {
		// TODO Auto-generated method stub
		for (SimulationListener sl : simulationListener) {
			return sl.onEndDecision();
		}
		return false;
	}

	@Override
	public void onSimStop() {
		// TODO Auto-generated method stub
	}


	
	public Simulation clone(AgentEnvironment environment){
		Simulation sim=new Simulation();
		sim.release=release;
		super.clone(sim,environment);
		sim.subSimulation=null;
		sim.setAdditionalString("Sub Simulation");
		return sim;
	}

	@Override
	public void beforeActivate() {

		if(subSimulation!=null){
			subSimulation.initHistoryDecisions();
			subSimulation.cloneSim();
			subSimulation.setRunning(false);
			subSimulation.setDecision("", null);
		}
	}

	public SubSimulation getSubSimulation() {
		return subSimulation;
	}

	public void setSubSimulation(SubSimulation subSimulation) {
		this.subSimulation = subSimulation;
	}
}
