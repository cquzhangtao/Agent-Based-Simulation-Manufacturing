package manu.simulation.start;

import manu.agent.AgentBasedModel;
import manu.model.ManufactureModel;
import manu.scheduling.SubSimulation;
import manu.scheduling.training.gui.TrainingGui;
import manu.simulation.SimController;
import manu.simulation.Simulation;
import manu.simulation.gui.SimulatorGui;
import manu.simulation.result.SimulationResult;

public class MainEntry {
	public static void main(String[] args) {

		if (args[0].equals("simulation")) {
			ManufactureModel manuModel = new ManufactureModel();
			AgentBasedModel agentModel = new AgentBasedModel();
			SimulationResult results = new SimulationResult();

			Simulation simulator = new Simulation();
			agentModel.register(simulator);
			simulator.setSubSimulation(new SubSimulation(agentModel, simulator));
			//simulator.setSubSimulation(null);
			//new SampleCollector(simulator, manuModel, agentModel, results);
			SimController controller = new SimController(
					simulator, manuModel, agentModel, results);
			
			SimulatorGui gui = new SimulatorGui(controller);
			gui.setVisible(true);
		} else if(args[0].equals("training")) {
			TrainingGui gui = new TrainingGui();
			gui.setVisible(true);
		}

	}

}
