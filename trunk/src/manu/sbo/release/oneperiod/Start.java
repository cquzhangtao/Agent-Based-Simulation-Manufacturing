package manu.sbo.release.oneperiod;

import manu.sbo.algorithm.antcolony.AntColonyAlgorithm;
import manu.sbo.algorithm.antcolony.DecisionVaraibles;
import manu.sbo.algorithm.antcolony.EvaluationInterface;
import manu.sbo.algorithm.antcolony.Problem;
import manu.sbo.framework.iSimulation;

public class Start {
	public static void main(String[] args) {
		AntColonyAlgorithm alg = new AntColonyAlgorithm(getExample1());
		alg.run();
		System.out.println(alg.getBestSolution().toString());
	}
	
	
	public static Problem getExample1(){
		Problem problem = new Problem();
		problem.addDecisionVaraible(285, 5000);
		problem.addDecisionVaraible(160, 5000);
		problem.addDecisionVaraible(170, 5000);
		problem.setGenerationNum(50);
		problem.setAntNum(10);
		problem.setDecimal(4);
		problem.setMaximization(true);
		final iSimulation sim=new iSimulation("E:\\project\\AgentBasedSim\\model\\Bottelneck.mod");
		final SimEvaluation simEva=new SimEvaluation();
		problem.setEvaluation(new EvaluationInterface() {

			@Override
			public double evaluate(DecisionVaraibles decisionVaraibles,
					int generationIndex) {
				ReleaseSolution solution=new ReleaseSolution();
				solution.addThroughput(decisionVaraibles.get(0).getValue());
				solution.addThroughput(decisionVaraibles.get(1).getValue());
				solution.addThroughput(decisionVaraibles.get(2).getValue());
				solution.setPlannedTime(50);
				double result=simEva.evaluation(solution, sim);
				//System.out.println(result);
				return result;
			}
		});
		return problem;
	}

}
