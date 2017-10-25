package manu.sbo.framework;

import manu.simulation.result.SimulationResult;

public abstract class EvaluationAbstract {
	
	protected abstract double getValue(SimulationResult result);
	
	public double evaluation(SolutionInterface sol,iSimulation sim){
		SimConfig(sol,sim);
		sim.reset();
		sim.start();
		while(!sim.isSimDone()){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return getValue(sim.getResult());		
	}
	
	private void SimConfig(SolutionInterface sol,iSimulation sim){
		sol.simConfig(sim);
	}
	
	
}
