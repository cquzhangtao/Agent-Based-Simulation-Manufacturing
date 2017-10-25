package manu.sbo.framework;

import java.util.List;

import manu.model.component.Product;
import manu.simulation.SimController;

public class SimulationConfig {
	
	private SimController simulator;
	public SimulationConfig(SimController simulator){
		this.simulator=simulator;
	}
	
	public void setPlannedTime(double days){
		simulator.getSimulatorInfo().setPlanTime(days);
	}
	public void setReleaseIntervalDetail(List<Double> intervals){
		for(Product p:simulator.getManuModel().getProducts()){
			p.getReleaseIntervalTimeR().setParameter("mean", intervals.get(p.getIndex()));
		}
	}

}
