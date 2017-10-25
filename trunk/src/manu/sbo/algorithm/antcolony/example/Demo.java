package manu.sbo.algorithm.antcolony.example;

import manu.sbo.algorithm.antcolony.AntColonyAlgorithm;
import manu.sbo.algorithm.antcolony.DecisionVaraibles;
import manu.sbo.algorithm.antcolony.EvaluationInterface;
import manu.sbo.algorithm.antcolony.Problem;

public class Demo {
	public static void main(String[] args) {
		AntColonyAlgorithm alg = new AntColonyAlgorithm(getExample());
		alg.setEnchanceCoefficien(1);
		alg.run();
		System.out.println(alg.getBestSolution().toString());
	}


	public static double[] run(){
		
		AntColonyAlgorithm alg = new AntColonyAlgorithm(getExample());
		alg.setEnchanceCoefficien(1);
		alg.run();
		double[] q=new double[2];
		q[0]=alg.getBestSolution().getDecisionVaraibles().get(0).getValue();
		q[1]=alg.getBestSolution().getDecisionVaraibles().get(1).getValue();
		return q;
	}

	public static Problem getExample() {
		Problem problem = new Problem();
		problem.addDecisionVaraible(100, 0);
		problem.addDecisionVaraible(100, 0);
		problem.setGenerationNum(100);
		problem.setAntNum(100);
		problem.setDecimal(10);
		problem.setEvaluation(new EvaluationInterface() {

			@Override
			public double evaluate(DecisionVaraibles decisionVaraibles, int gei) {
				double x = decisionVaraibles.get(0).getValue();
				double y = decisionVaraibles.get(1).getValue();

				double r = 2 * (x * y + 8 / x + 8 / y);
				return r;
			}
		});
		return problem;
	}

	public static Problem getExample1() {
		Problem problem = new Problem();
		problem.addDecisionVaraible(5.12, -5.12);
		problem.addDecisionVaraible(5.12, -5.12);
		problem.setGenerationNum(100);
		problem.setAntNum(20);
		problem.setDecimal(10);
		problem.setMaximization(true);
		problem.setEvaluation(new EvaluationInterface() {

			@Override
			public double evaluate(DecisionVaraibles decisionVaraibles,int genindex) {
				double x = decisionVaraibles.get(0).getValue();
				double y = decisionVaraibles.get(1).getValue();
				double r = (1 * Math.pow(3 / (0.05 + x * x + y * y), 2) + Math
						.pow(x * x + y * y, 2));
				return r;
			}
		});
		return problem;
	}

}
