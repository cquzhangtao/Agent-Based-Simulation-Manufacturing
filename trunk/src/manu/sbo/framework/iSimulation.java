package manu.sbo.framework;

import java.io.File;

import manu.agent.AgentBasedModel;
import manu.model.ManufactureModel;
import manu.simulation.SimController;
import manu.simulation.Simulation;
import manu.simulation.SimulatorListener;
import manu.simulation.result.SimulationResult;

public class iSimulation {

	private SimController simulator;
	private boolean simDone = false;

	public iSimulation(String manuModelFile) {
		this.simulator = getSimulator(manuModelFile);
		simulator.setShowGui(false);
		simulator.addSimulationListener(new SimulatorListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onEnd() {
				// TODO Auto-generated method stub
				simDone = true;
				//System.out.println("Finished");
			}

			@Override
			public void onReset() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStateChange(String state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTimeAdvance(long currentTime, long beginTime) {
				// TODO Auto-generated method stub
				// System.out.println(currentTime);
			}
		});

	}

	public void start() {

		simulator.startSim();
	}

	public void reset() {
		simDone = false;
		simulator.resetSim();
	}

	public SimulationResult getResult() {
		return simulator.getResults();

	}

	private SimController getSimulator(String manuModelFile) {
		ManufactureModel manuModel = new ManufactureModel();
		AgentBasedModel agentModel = new AgentBasedModel();
		SimulationResult results = new SimulationResult();

		Simulation simulator = new Simulation();
		agentModel.register(simulator);

		// new SampleCollector(simulator, manuModel, agentModel, results);
		SimController con = new SimController(simulator, manuModel, agentModel, results);
		con.openModel(new File(manuModelFile));
		return con;
	}

	public boolean isSimDone() {
		return simDone;
	}

	public SimController getSimulator() {
		return simulator;
	}


}
