package manu.sbo.demo;

import java.util.ArrayList;
import java.util.List;

import manu.sbo.framework.iSimulation;
import manu.sbo.release.oneperiod.ReleaseSolution;
import manu.sbo.release.oneperiod.SimEvaluation;

public class Alogrithm {
	iSimulation sim=new iSimulation("E:\\project\\AgentBasedSim\\model\\Bottelneck.mod");
	public  void run(){
		List<ReleaseSolution> solutions=new ArrayList<ReleaseSolution>();
		ReleaseSolution solution=new ReleaseSolution();
		solution.addThroughput(10);
		solution.addThroughput(10);
		solution.addThroughput(10);
		solution.setPlannedTime(200);
		solutions.add(solution);
		for(ReleaseSolution sol:solutions){
			SimEvaluation eva = new SimEvaluation();
			System.out.println(eva.evaluation(sol, sim));			
		}
	}

}
