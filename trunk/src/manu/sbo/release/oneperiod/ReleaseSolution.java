package manu.sbo.release.oneperiod;

import java.util.ArrayList;
import java.util.List;

import manu.sbo.framework.SimulationConfig;
import manu.sbo.framework.SolutionInterface;
import manu.sbo.framework.iSimulation;

public class ReleaseSolution implements SolutionInterface{
	
	private List<Double> throughputs;
	private double plannedTime;//days
	
	public ReleaseSolution(){
		throughputs=new ArrayList<Double>();
	}

	public void addThroughput(double d){
		throughputs.add(d);
	}

	@Override
	public void simConfig(iSimulation sim) {
		SimulationConfig config=new SimulationConfig(sim.getSimulator());
		
		config.setPlannedTime(plannedTime);
		
//		ArrayList<Double> intervals = new ArrayList<Double>();
//		for(double d:throughputs){
//			intervals.add(plannedTime/d*24*60);
//		}
		config.setReleaseIntervalDetail(throughputs);
		
		
	}

	public double getPlannedTime() {
		return plannedTime;
	}

	public void setPlannedTime(double planedTime) {
		this.plannedTime = planedTime;
	}







}
